package com.example.itechartchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itechartchat.R
import com.example.itechartchat.coordinators.LoginFlowCoordinator
import com.example.itechartchat.coordinators.LoginFlowCoordinatorInterface

class MainActivity : AppCompatActivity() {
    private val loginCoordinator: LoginFlowCoordinatorInterface = LoginFlowCoordinator(this.supportFragmentManager)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginCoordinator.start()
    }
}