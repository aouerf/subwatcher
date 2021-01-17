package com.aouerfelli.subwatcher.util.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

private val customTabsIntentBuilder = CustomTabsIntent.Builder()
  .setShareState(CustomTabsIntent.SHARE_STATE_ON)
  .setUrlBarHidingEnabled(true)
  .setShowTitle(true)

fun Uri.launch(context: Context, startNewTask: Boolean = false) {
  val intent = customTabsIntentBuilder.build()
  if (startNewTask) {
    intent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }
  intent.launchUrl(context, this)
}
