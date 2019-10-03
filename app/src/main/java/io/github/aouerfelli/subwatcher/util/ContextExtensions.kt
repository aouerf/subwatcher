package io.github.aouerfelli.subwatcher.util

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

inline val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

@ColorInt
fun Context.getThemeColor(@AttrRes attrResId: Int, @ColorInt defaultValue: Int = Color.BLACK): Int {
    return with(obtainStyledAttributes(null, intArrayOf(attrResId))) {
        try {
            getColor(0, defaultValue)
        } finally {
            recycle()
        }
    }
}
