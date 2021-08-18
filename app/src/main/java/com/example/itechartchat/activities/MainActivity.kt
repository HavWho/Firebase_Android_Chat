package com.example.itechartchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itechartchat.fragmentstarters.LoginFragmentStarter
import com.example.itechartchat.fragmentstarters.SignUpFragmentStarter
import com.example.itechartchat.R
import com.example.itechartchat.fragments.LoginFragment
import com.example.itechartchat.fragments.SignUpFragment

class MainActivity : AppCompatActivity(), LoginFragmentStarter, SignUpFragmentStarter {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startLoginFragment()
    }

    override fun startLoginFragment() {
        val loginFragment = LoginFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, loginFragment).commit()
    }

    override fun startSignUpFragment() {
        val signUpFragment = SignUpFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, signUpFragment).commit()
    }
}