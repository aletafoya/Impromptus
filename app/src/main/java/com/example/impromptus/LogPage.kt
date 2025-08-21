package com.example.impromptus

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit

val USER_TOKEN = stringPreferencesKey("user_token")

// Suspend function (idle thread) to store the token
suspend fun storeData(context: Context, token: String) {
    context.dataStore.edit { pref ->
        pref[USER_TOKEN] = token
    }
}

@Composable
fun LogPage(navController: NavHostController) {
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
    // Local context for the composable
    val context = LocalContext.current
    // Scope for coroutines
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf("") } // String to retain the username on Text Field
    var pass by remember { mutableStateOf("") } // String to retain the password on Text Field
    var notCouple by remember { mutableStateOf(false) } // Variable to store if the password and username match
    var usExists by remember { mutableStateOf(false) } // Variable to store if the username exists
    var showMatch by remember { mutableStateOf(true) } // Variable to store if the password and username match
    var showExists by remember { mutableStateOf(true) } // Variable to store if the username exists
    var permission by remember { mutableStateOf(false) } // Variable to trigger the change of screen
    var parameters by remember { mutableStateOf(true) } // Variable to store if the parameters are valid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
                    value = user, onValueChange = { user = it; showExists = true; showMatch = true },
                    label = { Text("Username", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                if(!showExists) {
                    Text(
                        "Username does not exist",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                TextField(
                    value = pass, onValueChange = { pass = it; showMatch = true; showExists = true },
                    label = { Text("Password", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                if(!parameters) {
                    Text(
                        "Parameters are required",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                ElevatedButton (
                    onClick = {
                        Log.d("BUTTON", "Button clicked")
                        if(user == "" || pass == "") {
                            parameters = false
                        }
                        else {
                            parameters = true
                            queryToEnter(
                                username = user,
                                password = pass,
                                granted = { newValue ->
                                    permission = newValue
                                    Log.d("OK", "Permission: $permission")
                                    if (permission) {
                                        Log.d("BUTTON", "Permission granted")
                                        scope.launch(Dispatchers.IO) {
                                            storeData(context, user)
                                        }

                                        change(true)
                                        showMatch = true
                                        showExists = true
                                    }
                                  },
                                match = { newValue -> notCouple = newValue
                                    if(!notCouple) {
                                        showMatch = false
                                        showExists = true
                                    }
                                },
                                exists = { newValue -> usExists = newValue
                                    if(!usExists) {
                                        showExists = false
                                        showMatch = true
                                     }
                                    }
                                )
                            }
                        },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Login", fontFamily = FontFamily.Monospace)
                }

                if(!showMatch) {
                    Text(
                        "Password and username do not match",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
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
    // The context of the composable is local
    val context = LocalContext.current
    // Scope for coroutines
    val scope = rememberCoroutineScope()
    var mail by remember {mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmP by remember {mutableStateOf("") }
    var showMatch by remember { mutableStateOf(true) }
    var showExists by remember { mutableStateOf(true) }
    var showUsed by remember { mutableStateOf(true)}
    var blank by remember { mutableStateOf(false) }
    var valid by remember { mutableStateOf(true) }
    var enterEm by remember { mutableStateOf(true) }
    var enterUs by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(600.dp)
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
                    value = mail, onValueChange = { mail = it; showUsed = true; showExists = true; showMatch = true },
                    label = { Text("Email", fontFamily = FontFamily.Monospace) }
                )

                if(!enterEm) {
                    Text(
                        "Enter Email",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(start = 16.dp))
                }

                if(!showUsed) {
                    Text(
                        "Email is already used",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(start = 16.dp))
                }

                TextField(
                    value = user, onValueChange = { user = it; showUsed = true; showExists = true; showMatch = true },
                    label = { Text("Username", fontFamily = FontFamily.Monospace) }
                )

                if(!enterUs) {
                    Text(
                        "Enter Username",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                    )
                }

                if(!showExists) {
                    Text(
                        "Username is already used",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(start = 16.dp))
                }

                TextField(
                    value = pass, onValueChange = { pass = it; showMatch = true; showExists = true; showUsed = true },
                    label = { Text("Password", fontFamily = FontFamily.Monospace) }
                )

                if(blank) {
                    Text(
                        "Password cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(start = 16.dp))
                }

                TextField(
                    value = confirmP, onValueChange = { confirmP = it; showMatch = true; showExists = true; showUsed = true },
                    label = { Text("Confirm Password", fontFamily = FontFamily.Monospace) }
                )

                if(!valid) {
                    Text(
                        "Passwords does not match",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(start = 16.dp))
                }

                ElevatedButton (
                    onClick = {
                        // --- Stage 1: Client-side validation (synchronous) ---
                        val currentPass = pass // Capture current values
                        val currentConfirmP = confirmP
                        val currentMail = mail
                        val currentUser = user

                        val isPassBlank = currentPass == ""
                        val passwordsCurrentlyMatch = currentPass == currentConfirmP
                        val isMailEmpty = currentMail == ""
                        val isUserEmpty = currentUser == ""

                        // Update UI for blank fields immediately
                        blank = isPassBlank
                        valid = passwordsCurrentlyMatch
                        enterEm = !isMailEmpty
                        enterUs = !isUserEmpty

                        if (isPassBlank || !passwordsCurrentlyMatch || isMailEmpty || isUserEmpty) {
                            Log.d("REG_VALIDATION", "Client-side validation failed. blank=$isPassBlank, match=$passwordsCurrentlyMatch, mailEmpty=$isMailEmpty, userEmpty=$isUserEmpty")
                            if (!passwordsCurrentlyMatch) showMatch = false else showMatch = true
                        }

                        // --- Stage 2: Server-side uniqueness check (asynchronous) ---
                        Log.d("REG_BUTTON_CLICK", "Proceeding to queryToVerifyUniqueness")
                        queryToVerifyUniqueness(
                            currentMail,
                            currentUser,
                            emailExist = { isEmailAvailable ->
                                Log.d("REG_LAMBDA", "emailExist (isEmailAvailable) called: $isEmailAvailable")
                                // Update UI state for email uniqueness.
                                // This will trigger recomposition.
                                showUsed = !isEmailAvailable
                            },
                            usernameExist = { isUsernameAvailable ->
                                Log.d("REG_LAMBDA", "usernameExist (isUsernameAvailable) called: $isUsernameAvailable")
                                showExists = !isUsernameAvailable
                            },
                            access = { canProceedWithRegistration -> // This lambda means "email AND username are unique"
                                Log.d("REG_LAMBDA", "access (canProceedWithRegistration) called with: $canProceedWithRegistration")

                                val finalPasswordsMatch = currentPass == currentConfirmP
                                showMatch = finalPasswordsMatch // Update UI for password match

                                Log.d("REG_FINAL_CHECK", "canProceedWithRegistration=$canProceedWithRegistration, finalPasswordsMatch=$finalPasswordsMatch, !isPassBlank=${!isPassBlank}")

                                if (canProceedWithRegistration && finalPasswordsMatch && !isPassBlank) {
                                    Log.d("REG_SUCCESS", "All conditions met for registration.")

                                    scope.launch(Dispatchers.IO) {
                                        storeData(context, user)
                                    }

                                    change(true) // Tell LogPage to navigate
                                    queryToInsert(currentMail, currentUser, currentPass)
                                } else {
                                    Log.d("REG_FAIL",
                                        "Registration failed after API check. canProceed=$canProceedWithRegistration, " +
                                                "passMatch=$finalPasswordsMatch")
                                }
                            }
                        )
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
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
fun queryToEnter(username: String, password: String, granted: (Boolean) -> Unit, match: (Boolean) -> Unit,
                 exists: (Boolean) -> Unit) {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("http://10.0.2.2:8080/user?username=$username")
        .build()

    // Coroutine (asynchronous thread) to make a request to the server,
    // this allow us to not block the main thread and the UI,
    // thus, we avoid the "App is not responding" error
    CoroutineScope(Dispatchers.IO).launch {
            // Dispatchers IO means:
        // “Run this work on a background thread pool optimized for disk/network I/O.”
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()

                    if(body.toString() != "null") {
                        // Using GSON library to create an object and parse the JSON
                        val gson = Gson()
                        val user : UserStructure = gson.fromJson(body, UserStructure::class.java)

                        if(user.password_hash == password) {
                            Log.d("RESPONSE", "Password match")
                            withContext(Dispatchers.Main) {
                                granted(true)
                                exists(true)
                                match(true)
                            }
                        }
                        else {
                            // this return to the main thread to make this a priority task
                            withContext(Dispatchers.Main) {
                                granted(false)
                                exists(true)
                                match(false)
                            }
                        }

                        return@launch
                    } else {
                        withContext(Dispatchers.Main) {
                            granted(false)
                            exists(false)
                            match(true)
                        }
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

// Function to verify if the username and email are already used and the form
// is valid
fun queryToVerifyUniqueness(email: String, username: String, emailExist: (Boolean) -> Unit,
                           usernameExist: (Boolean) -> Unit, access: (Boolean) -> Unit) {
    // Client
    val client = OkHttpClient()

    // Request to the server
    val request = Request.Builder()
        .url("http://10.0.2.2:8080/uniqueRegistration?email=$email&username=$username")
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            client.newCall(request).execute().use { response ->
                if(response.isSuccessful) {
                    val body = response.body?.string()
                    var foundEmail = false
                    var foundUsername = false

                    if(body.toString() != "null") {
                        Log.d("Response", body.toString())

                        val gson = Gson()
                        // We made another DataClass to store all the data
                        val listType = gson.fromJson(body, APIResponse::class.java)
                        // We treat every request as a list of users, in case there is a match
                        // between usernames and emails.
                        val users: List<UserStructure> = listType.data

                        // Example: log each username
                        for (user in users) {
                            if(user.email == email) foundEmail = true
                            if(user.username == username) foundUsername = true
                        }

                        withContext(Dispatchers.Main) {
                            if(foundEmail) emailExist(false)
                            else emailExist(true)
                            if(foundUsername) usernameExist(false)
                            else usernameExist(true)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            access(true)
                        }
                        Log.e("UNIQUE USER", "NO USERS FOUND")
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

// Function to trigger the API request
fun queryToInsert(email: String, username: String, password: String) {
    val client = OkHttpClient()

    val jsonFormat = """
        {
            "email": "$email",
            "username": "$username",
            "password": "$password"
        }
    """.trimIndent()

    val requestBody = jsonFormat.toRequestBody("application/json".toMediaType())
    // Request to the server
    val request = Request.Builder()
        .url("http://10.0.2.2:8080/newUser")
        .post(requestBody)
        .build()

    // Same secondary Thread
    CoroutineScope(Dispatchers.IO).launch {
        // Execution of the request
        client.newCall(request).execute().use { response ->
            if(response.isSuccessful) {
                val body = response.body?.string()
                Log.d("RESPONSE", "User inserted")
                Log.d("JSON FROM API", body.toString())
            } else {
                Log.e("NOT_INSERTED", "Request failed with code ${response.code}")
            }
        }
    }
}