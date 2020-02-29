package io.github.aouerfelli.subwatcher.util.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use

inline val Context.layoutInflater: LayoutInflater
  get() = LayoutInflater.from(this)

tailrec fun Context.getActivityContext(): Context? {
  return when (this) {
    !is ContextWrapper -> null
    is Activity -> this
    else -> baseContext.getActivityContext()
  }
}

@ColorInt
fun Context.getThemeColor(@AttrRes attrResId: Int, @ColorInt defaultValue: Int = Color.BLACK): Int {
  return obtainStyledAttributes(null, intArrayOf(attrResId)).use { it.getColor(0, defaultValue) }
}
