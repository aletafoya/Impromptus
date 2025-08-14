package com.example.impromptus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun Dashboard(modifier: Modifier, navController: NavHostController) {
    Row (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow),
    ) {
        Text (
            "Welcome to Colmena Academy",
            modifier.padding(25.dp).border(1.dp, Color.Black, RoundedCornerShape(10.dp))
        )
        Box(
            modifier = Modifier
                .background(Color.White)
                .height(200.dp)
        ) {
            Button (onClick = { navController.navigate(Routes.logPage) }, modifier = Modifier.align(Alignment.Center)) {

            }
        }
    }

}
