package com.example.itechartchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itechartchat.R
import com.example.itechartchat.other.Navigator

class MainActivity : AppCompatActivity() {
    private var navigator: Navigator = Navigator()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigator.activity = this
        navigator.showLogInFragment()
    }

    override fun onDestroy() {
        navigator.unsetActivity()
        super.onDestroy()
    }
}