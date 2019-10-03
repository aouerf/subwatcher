package io.github.aouerfelli.subwatcher.util

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.aouerfelli.subwatcher.R

fun SwipeRefreshLayout.setThemeColorScheme() {
    val foregroundColor = context.getThemeColor(R.attr.colorSecondary)
    val backgroundColor = context.getThemeColor(R.attr.colorBackgroundFloating)
    setColorSchemeColors(foregroundColor)
    setProgressBackgroundColorSchemeColor(backgroundColor)
}
