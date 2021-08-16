package com.example.itechartchat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity(), LoginFragmentStarter {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startLoginFragment()
    }

    override fun startLoginFragment() {
        val loginFragment = LoginFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, loginFragment).commit()
    }
}