package com.example.gpt.utils

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 2

fun LazyListState.lastItemPosition() : Float =
    layoutInfo.visibleItemsInfo.lastOrNull()?.size?.toFloat() ?: 0F