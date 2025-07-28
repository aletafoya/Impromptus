package com.example.impromptus

import android.os.Bundle
import android.text.Layout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impromptus.ui.theme.ImpromptusTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.GenericFontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImpromptusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppContent(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppContent(modifier: Modifier) {
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        Reg(back = { showRegister = false })
    } else {
        Login(reg = { showRegister = true })
    }
}

@Composable
fun Login(reg: () -> Unit) {
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
                    value = "", onValueChange = {},
                    label = { Text("Username", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                TextField(
                    value = "", onValueChange = {},
                    label = { Text("Password", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Button(
                    onClick = {}, modifier = Modifier
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
fun Reg(back: () -> Unit) {
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
                    value = "", onValueChange = {},
                    label = { Text("Email", fontFamily = FontFamily.Monospace) }
                )

                TextField(
                    value = "", onValueChange = {},
                    label = { Text("Full Name", fontFamily = FontFamily.Monospace) }
                )

                TextField(
                    value = "", onValueChange = {},
                    label = { Text("Password", fontFamily = FontFamily.Monospace) }
                )

                TextField(
                    value = "", onValueChange = {},
                    label = { Text("Confirm Password", fontFamily = FontFamily.Monospace) }
                )

                Button(onClick = {}, modifier = Modifier.padding(16.dp)) {
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

