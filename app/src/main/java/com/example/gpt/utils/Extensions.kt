package com.example.gpt.utils

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 2

fun LazyListState.lastItemPosition() : Float =
    layoutInfo.visibleItemsInfo.lastOrNull()?.size?.toFloat() ?: 0F

fun String.mask() =
    if (length <= 3) {
        replaceRange(1, length, "*".repeat(length - 1))
    } else {
        replaceRange(3, length, "*".repeat(length - 3))
    }
