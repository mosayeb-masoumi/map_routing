package com.mosayebmaprouting.mapapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mosayebmaprouting.mapapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

//    override fun onBackPressed() {
//        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
//        if (fragment is HomeFragment) {
//            finish() // Exit the app
//        } else {
//            super.onBackPressed() // Default behavior (go back)
//        }
//    }
}