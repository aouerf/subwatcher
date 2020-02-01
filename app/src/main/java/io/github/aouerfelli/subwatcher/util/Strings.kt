package io.github.aouerfelli.subwatcher.util

import android.content.Context
import androidx.annotation.StringRes

sealed class AndroidString {
  data class Res(@StringRes val resId: Int) : AndroidString()
  data class Raw(val string: String) : AndroidString()

  fun getString(context: Context): String {
    return when (this) {
      is Res -> context.getString(resId)
      is Raw -> string
    }
  }
}

fun @receiver:StringRes Int.toAndroidString() = AndroidString.Res(this)

fun String.toAndroidString() = AndroidString.Raw(this)
