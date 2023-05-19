package com.example.gpt.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.gpt.R

sealed class Screen(@StringRes val route: Int, @DrawableRes val icon: Int) {
    object Chat : Screen(R.string.chat_nav, R.drawable.question_answer_icon)
    object Image : Screen(R.string.image_nav, R.drawable.image_icon)
    object Audio : Screen(R.string.audio_nav, R.drawable.audio_icon)
    object Settings : Screen(R.string.settings_nav, R.drawable.settings_icon)
}
