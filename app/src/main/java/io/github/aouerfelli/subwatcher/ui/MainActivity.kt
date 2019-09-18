package io.github.aouerfelli.subwatcher.ui

import android.os.Bundle
import androidx.fragment.app.commitNow
import androidx.fragment.app.replace
import dagger.android.support.DaggerAppCompatActivity
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.ui.main.MainFragment

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState != null) {
            supportFragmentManager.commitNow {
                replace<MainFragment>(R.id.container)
            }
        }
    }
}
