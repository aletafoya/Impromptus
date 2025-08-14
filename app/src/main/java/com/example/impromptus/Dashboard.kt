package com.example.impromptus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun Dashboard(modifier: Modifier, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navController.navigate(Routes.logPage) }) {
            Text("Go to Log Page")
        }
    }
}
