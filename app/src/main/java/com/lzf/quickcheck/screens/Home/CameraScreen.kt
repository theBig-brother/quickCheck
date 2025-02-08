package com.lzf.quickcheck.screens.Home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

fun CameraScreen(navController: NavController) {

}
@Preview(showBackground = true)
@Composable
fun PreviewCameraScreen() {
    val navController = rememberNavController()
    CameraScreen(navController)
}

