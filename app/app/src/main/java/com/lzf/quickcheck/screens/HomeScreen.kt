package com.lzf.quickcheck.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(navController: NavController) {
    // 页面布局：使用 Column 垂直排列按钮
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),  // 设置整体 padding
        horizontalAlignment = Alignment.CenterHorizontally,  // 水平居中
        verticalArrangement = Arrangement.Center  // 垂直居中
    ) {
        // Camera 按钮，点击进入相机页面
        Button(
            onClick = { navController.navigate("camera") }, // 跳转到 CameraScreen
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp) // 设置按钮的阴影
        ) {
            Text("Camera", style = MaterialTheme.typography.headlineSmall) // 按钮文字
        }

        // ToDo 按钮，点击进入待开发页面
        Button(
            onClick = { navController.navigate("todo") }, // 跳转到 ToDo 页面
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("ToDo", style = MaterialTheme.typography.headlineSmall) // 按钮文字
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    // HomeScreen 预览，传入一个假的 NavController
    val navController = rememberNavController()
    HomeScreen(navController)
}
