package io.github.aouerfelli.subwatcher.util

import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import io.github.aouerfelli.subwatcher.R

fun SwipeRefreshLayout.setThemeColorScheme() {
    val foregroundColor = context.getThemeColor(R.attr.colorSecondary)
    val backgroundColor = context.getThemeColor(R.attr.colorBackgroundFloating)
    setColorSchemeColors(foregroundColor)
    setProgressBackgroundColorSchemeColor(backgroundColor)
}

fun View.makeSnackbar(
    @StringRes textStringRes: Int, length: Int = Snackbar.LENGTH_SHORT,
    show: Boolean = true
): Snackbar {
    return Snackbar.make(this, textStringRes, length).apply {
        if (show) {
            show()
        }
    }
}
