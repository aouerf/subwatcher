package io.github.aouerfelli.subwatcher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.aouerfelli.subwatcher.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
