package com.aouerfelli.subwatcher.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aouerfelli.subwatcher.R

class MainActivity : AppCompatActivity(R.layout.main_activity) {

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_Subwatcher)
    super.onCreate(savedInstanceState)

    if (Build.VERSION.SDK_INT == 26 && resources.getBoolean(R.bool.is_light_mode)) {
      window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
          // Light navigation bar must be enabled in code instead of theme for API 26
          View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
  }
}
