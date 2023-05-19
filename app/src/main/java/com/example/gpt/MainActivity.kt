package com.example.gpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gpt.ui.composable.screen.AudioMessageScreen
import com.example.gpt.ui.composable.screen.ChatMessageScreen
import com.example.gpt.ui.composable.screen.ImageMessageScreen
import com.example.gpt.ui.composable.screen.SettingsScreen
import com.example.gpt.ui.navigation.Screen
import com.example.gpt.ui.theme.GptTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GptTheme {
                ShowApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(Screen.Chat, Screen.Image, Screen.Audio, Screen.Settings)

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val currentRoute = context.getString(screen.route)
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.icon),
                                    contentDescription = currentRoute
                                )
                            },
                            label = { Text(currentRoute) },
                            selected = currentDestination?.hierarchy?.any { it.route == currentRoute } == true,
                            onClick = { navController.navigate(currentRoute) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = context.getString(Screen.Chat.route),
                Modifier.padding(innerPadding)
            ) {
                composable(context.getString(Screen.Chat.route)) { ChatMessageScreen() }
                composable(context.getString(Screen.Image.route)) { ImageMessageScreen() }
                composable(context.getString(Screen.Audio.route)) { AudioMessageScreen() }
                composable(context.getString(Screen.Settings.route)) { SettingsScreen() }
            }
        }
    }
}
