package io.github.aouerfelli.subwatcher.ui

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import io.github.aouerfelli.subwatcher.databinding.MainActivityBinding

class MainActivity : DaggerAppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
