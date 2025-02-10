package com.lzf.quickcheck


import android.Manifest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.navigation.navDeepLink
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.lzf.quickcheck.screens.*  // 引入页面
import com.lzf.quickcheck.screens.home.*
import com.lzf.quickcheck.screens.functions.*
import kotlinx.coroutines.delay
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp() // 设置应用的主界面为 MyApp Composable
        }
    }
}
fun String.encode() = URLEncoder.encode(this, StandardCharsets.UTF_8.toString()) ?: ""

fun String.decode() = URLDecoder.decode(this, StandardCharsets.UTF_8.toString()) ?: ""

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyApp() {
    // 创建 NavController 进行导航控制
    val navController = rememberNavController()
// 启动时请求权限
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val storagePermissionState = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val internetPermissionState = rememberPermissionState(permission = Manifest.permission.INTERNET)

    // 启动画面，2 秒后跳转到主界面

    var showSplash by remember { mutableStateOf(false) } // 定义状态控制是否显示 Splash 屏幕
    LaunchedEffect(Unit) {
        if(showSplash){
            delay(2000) // 延迟 2 秒
            showSplash = false // 2 秒后切换状态，隐藏 Splash 屏幕
        }

        // 请求相机和存储权限
        cameraPermissionState.launchPermissionRequest()
        storagePermissionState.launchPermissionRequest()
        internetPermissionState.launchPermissionRequest()
    }

    val screens: List<Pair<String, @Composable () -> Unit>> = listOf(
        "home" to { HomeScreen(navController) },
        "function" to { FunctionScreen(navController) },
        "mine" to { MineScreen() },
        "camera" to { CameraScreen(navController) },
        "todo" to { ToDoScreen(navController) },
        "pictures" to { Pictures(navController) }
    )

    if (showSplash) {
        SplashScreen() // 显示 Splash 屏幕
    } else {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) } // 底部导航栏
        ) { paddingValues ->
            NavHost(
                navController = navController, // 导航控制器
                startDestination = "home", // 启动时默认显示 "home" 页面
                modifier = Modifier.padding(paddingValues) // 设置内容的 padding
            ) {

                screens.forEach { (route, screen) ->
                    composable(route) { screen() }
                }
            }
        }
    }
}