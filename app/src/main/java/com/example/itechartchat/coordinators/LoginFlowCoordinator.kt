package com.example.itechartchat.coordinators

import androidx.fragment.app.FragmentManager
import com.example.itechartchat.R
import com.example.itechartchat.fragments.ChatsFragment
import com.example.itechartchat.fragments.ForgotPasswordFragment
import com.example.itechartchat.fragments.LoginFragment
import com.example.itechartchat.fragments.SignUpFragment
import com.example.itechartchat.viewmodels.ChatsViewModel
import com.example.itechartchat.viewmodels.ForgotPasswordViewModel
import com.example.itechartchat.viewmodels.LoginViewModel
import com.example.itechartchat.viewmodels.SignUpViewModel

interface LoginFlowCoordinatorInterface: CoordinatorInterface {
    //TODO: create fun startChats()
    fun startLogInFragment()
    fun finishLogInFragment()
    fun startSignUpFragment()
    fun finishSignUpFragment()
    fun startForgotPasswordFragment()
    fun finishForgotPasswordFragment()
}

interface SignUpFlowCoordinatorInterface: CoordinatorInterface {
    fun startSignUpFragment()
    fun finishSignUpFragment()
}

interface ForgotPasswordFlowCoordinatorInterface: CoordinatorInterface {
    fun startForgotPasswordFragment()
    fun finishForgotPasswordFragment()
}

class LoginFlowCoordinator(val activity: FragmentManager): CoordinatorInterface, SignUpFlowCoordinatorInterface,
    LoginFlowCoordinatorInterface, ForgotPasswordFlowCoordinatorInterface {

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
        val viewModel = ChatsViewModel(this)
        activity.beginTransaction()
            .replace(R.id.container, ChatsFragment(viewModel))
            .commit()
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

    override fun finishForgotPasswordFragment() {
        startLogInFragment()
    }

    override fun startForgotPasswordFragment() {
        val viewModel = ForgotPasswordViewModel(this)
        activity.beginTransaction()
            .replace(R.id.container, ForgotPasswordFragment(viewModel))
            .commit()
    }

}