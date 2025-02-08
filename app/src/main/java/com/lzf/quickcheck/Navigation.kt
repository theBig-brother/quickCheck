package com.lzf.quickcheck

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// 定义导航项
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object Function : BottomNavItem("function", Icons.Filled.Build, "Function")
    object Mine : BottomNavItem("mine", Icons.Filled.Person, "Mine")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.Function, BottomNavItem.Mine)
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}
