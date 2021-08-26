package com.example.itechartchat.coordinators

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.itechartchat.R
import com.example.itechartchat.fragments.LoginFragment
import com.example.itechartchat.fragments.SignUpFragment
import com.example.itechartchat.viewmodels.LoginViewModel
import com.example.itechartchat.viewmodels.SignUpViewModel

interface LoginFlowCoordinatorInterface: CoordinatorInterface {
    //TODO: create fun startChats()
    fun startLogInFragment()
    fun finishLogInFragment()
    fun startSignUpFragment()
    //TODO: fun startForgotPasswordFragment()
}

interface SignUpFlowCoordinatorInterface: CoordinatorInterface {
    fun startSignUpFragment()
    fun finishSignUpFragment()
}

interface ForgotPasswordFlowCoordinatorInterface: CoordinatorInterface {
    fun startForgotPasswordFragment()
}

class LoginFlowCoordinator(val activity: FragmentManager): CoordinatorInterface, SignUpFlowCoordinatorInterface,
    LoginFlowCoordinatorInterface {

    override fun startLogInFragment() {
        val viewModel = LoginViewModel(this)
        activity
            .beginTransaction()
            .replace(R.id.container, LoginFragment(viewModel))
            .commit()
    }

    override fun start() {
        startLogInFragment()
    }

    override fun finish() {

    }

    override fun finishLogInFragment() {
        activity.executePendingTransactions()
    }

    override fun startSignUpFragment() {
        val viewModel = SignUpViewModel(this)
        activity.beginTransaction()
            .replace(R.id.container, SignUpFragment(viewModel))
            .commit()
    }

    override fun finishSignUpFragment() {
        startLogInFragment()
    }

}