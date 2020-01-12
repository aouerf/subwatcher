package io.github.aouerfelli.subwatcher.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

private val customTabsIntentBuilder = CustomTabsIntent.Builder()
  .addDefaultShareMenuItem()
  .enableUrlBarHiding()
  .setShowTitle(true)

fun Uri.launch(context: Context) {
  val intent = customTabsIntentBuilder.build()
  intent.launchUrl(context, this)
}
