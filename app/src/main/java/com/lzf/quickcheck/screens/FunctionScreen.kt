package com.lzf.quickcheck.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun FunctionScreen(navController: NavController) {
    // 页面布局：使用 Column 垂直排列按钮
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),  // 设置整体 padding
        horizontalAlignment = Alignment.CenterHorizontally,  // 水平居中
        verticalArrangement = Arrangement.Center  // 垂直居中
    ) {
        // picture 按钮，点击进入相机页面
        Button(
            onClick = { navController.navigate("pictures") }, // 跳转到 CameraScreen
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp) // 设置按钮的阴影
        ) {
            Text("图库", style = MaterialTheme.typography.headlineSmall) // 按钮文字
        }
    }
}
