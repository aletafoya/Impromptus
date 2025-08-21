package com.example.impromptus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.impromptus.LogPage
import com.example.impromptus.Dashboard
import com.example.impromptus.ui.theme.ImpromptusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImpromptusTheme {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Routes.logPage, builder = {
                        composable(Routes.logPage) {
                            LogPage(navController)
                        }
                        composable(Routes.dashboard) {
                            Dashboard(navController, "")
                        }
                    }
                )
            }
        }
    }
}




