package com.example.impromptus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.*
import java.io.IOException
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun LogPage(modifier: Modifier, navController: NavHostController) {
    var showRegister by remember { mutableStateOf(false) }
    var permissionToAccess by remember { mutableStateOf(false) }

    if(permissionToAccess) navController.navigate(Routes.dashboard)
    if (showRegister) {
        Reg(back = { showRegister = false }, change = { newValue -> permissionToAccess = newValue })
    } else {
        Login(reg = { showRegister = true }, change = { newValue -> permissionToAccess = newValue })
    }
}

@Composable
fun Login(reg: () -> Unit, change: (Boolean) -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(500.dp)
                .background(Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    "Login",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                TextField(
                    value = user, onValueChange = { user = it },
                    label = { Text("Username", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                TextField(
                    value = pass, onValueChange = { pass = it },
                    label = { Text("Password", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Button(
                    onClick = {
                        Log.d("BUTTON", "Button clicked")
                        val permission = queryToEnter(user, pass)

                        if(permission) change(true)
                    },
                    modifier = androidx.compose.ui.Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Login", fontFamily = FontFamily.Monospace)
                }

                Text(
                    "Don't have an account?",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable { reg() }
                )
            }
        }
    }
}

@Composable
fun Reg(back: () -> Unit, change: (Boolean) -> Unit) {
    var mail by remember {mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmP by remember {mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(500.dp)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Register", fontSize = 30.sp, fontFamily = FontFamily.Monospace)

                TextField(
                    value = mail, onValueChange = { mail = it },
                    label = { Text("Email", fontFamily = FontFamily.Monospace) }
                )

                TextField(
                    value = user, onValueChange = { user = it },
                    label = { Text("Username", fontFamily = FontFamily.Monospace) }
                )

                TextField(
                    value = pass, onValueChange = { pass = it },
                    label = { Text("Password", fontFamily = FontFamily.Monospace) }
                )

                TextField(
                    value = confirmP, onValueChange = { confirmP = it },
                    label = { Text("Confirm Password", fontFamily = FontFamily.Monospace) }
                )

                Button(onClick = { queryToVerifyUniqueness(mail, user) }, modifier = Modifier.padding(16.dp)) {
                    Text("Register", fontFamily = FontFamily.Monospace)
                }

                Text(
                    "Already have an account?",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { back() }
                        .padding(8.dp)
                )
            }
        }
    }
}

// Client API
fun queryToEnter(username: String, password: String) : Boolean {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("http://10.0.2.2:8080/user?username=$username")
        .build()

    // Coroutine (second thread) to make a request to the server,
    // this allow us to not block the main thread and the UI,
    // thus, we avoid the "App is not responding" error
    var permission = false
    CoroutineScope(Dispatchers.IO).launch {
            // Dispatchers IO means:
        // “Run this work on a background thread pool optimized for disk/network I/O.”
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    // This is shown on the logcat
                    Log.d("RESPONSE", body.toString())

                    if(body != null) {
                        // Using GSON library to create an object and parse the JSON
                        val gson = Gson()
                        val user : UserStructure = gson.fromJson(body, UserStructure::class.java)

                        permission = password == user.password_hash

                        return@launch

                    } else {
                        Log.e("RESPONSE", "Response body is null")
                    }
                } else {
                    Log.e("RESPONSE", "Request failed with code ${response.code}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    return permission
}

fun queryToVerifyUniqueness(email: String, username: String) {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("http://10.0.2.2:8080/uniqueRegistration?email=$email&username=$username")
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            client.newCall(request).execute().use { response ->
                if(response.isSuccessful) {
                    val body = response.body?.string()

                    if(body != null) {
                        Log.d("Response", body.toString())

                        val gson = Gson()
                        // We made another DataClass to store all the data
                        val listType = gson.fromJson(body, APIResponse::class.java)
                        // We treat every request as a list of users, in case there is a match
                        // between usernames and emails.
                        val users: List<UserStructure> = listType.data

                        // Example: log each username
                        for (user in users) {
                            Log.d("USER", "Username: ${user.username}, Pass: ${user.password_hash}")
                        }
                    } else {
                        Log.e("RESPONSE", "Response body is null")
                    }
                } else {
                    Log.e("RESPONSE", "Request failed with code ${response.code}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
