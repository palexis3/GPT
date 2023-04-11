package com.example.gpt.ui.navigation

import androidx.annotation.DrawableRes
import com.example.gpt.R

sealed class Screen(val route: String, @DrawableRes val icon: Int) {
    object Chat : Screen("chat", R.drawable.question_answer_icon)
    object Image : Screen("image", R.drawable.image_icon)
    object Audio : Screen("audio", R.drawable.audio_icon)
    object Settings : Screen("settings", R.drawable.settings_icon)
}
