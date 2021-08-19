package com.example.itechartchat.coordinators

import com.example.itechartchat.other.Navigator

class LoginFlowCoordinator {

    private val navigator = Navigator()

    fun start() {
        navigator.showLogInFragment()
    }

    fun registerNewUser() {
        navigator.showSignUpFragment()
    }

    /*fun forgotPassword() {

    }*/

}