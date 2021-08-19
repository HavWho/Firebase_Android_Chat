package com.example.itechartchat.other

import androidx.fragment.app.FragmentActivity
import com.example.itechartchat.R
import com.example.itechartchat.activities.MainActivity
import com.example.itechartchat.fragments.LoginFragment
import com.example.itechartchat.fragments.SignUpFragment

class Navigator {

    var activity : FragmentActivity? = null

    fun unsetActivity() {
        activity = null
    }

    fun showLogInFragment() {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, LoginFragment())
            .commit()
    }

    fun showSignUpFragment() {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, SignUpFragment())
            .commit()
    }

    /*fun showRecoveryPassword() {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ForgotPasswordFragment())
    }*/

}