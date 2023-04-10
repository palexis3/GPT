package com.example.gpt.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val iconId: ImageVector) {
    object Chat : Screen("chat", Icons.Outlined.List)
    object Image : Screen("image", Icons.Outlined.Phone)
}
