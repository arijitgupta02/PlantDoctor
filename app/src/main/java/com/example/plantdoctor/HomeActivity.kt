package com.example.plantdoctor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.plantdoctor.databinding.ActivityHomeBinding
import com.example.plantdoctor.ui.home.HomeFragment


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.homeFragmentContainer.id, HomeFragment())
            .commit()
    }
}
