package com.example.itechartchat.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.example.itechartchat.viewmodels.LoginViewModel
import com.example.itechartchat.R
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */

class LoginFragment : Fragment() {

    private lateinit var emailTextInput : TextInputEditText
    private lateinit var passwordTextInput : TextInputEditText
    private lateinit var loginButton : Button
    private lateinit var signUpButton : AppCompatTextView
    private val loginViewModel = LoginViewModel()

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        emailTextInput = view.findViewById(R.id.email_textinputedittext)
        passwordTextInput = view.findViewById(R.id.password_textinputedittext)
        loginButton = view.findViewById(R.id.login_button)
        signUpButton = view.findViewById(R.id.signUpText)

        Log.d("Fragment","onCreateView")

        //isValidEditText(mEmailTextInput.text.toString().trim())
        // Inflate the layout for this fragment
        return view
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()

        Log.d("Fragment", "onResume")

        loginViewModel.canLogIn
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                loginButton.isClickable = it
            }

        loginViewModel.canLogIn
            .map {
                if (it) 1.0f else 0.5f
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                loginButton.alpha = it
            }

        loginViewModel.emailValid
            .filter {
                it.equals(false)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                emailTextInput.setError(getString(R.string.emailError), null)
            }

        loginViewModel.passwordValid
            .filter {
                it.equals(false)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                passwordTextInput.setError(getString(R.string.passwordError), null)
            }

        loginButton.setOnClickListener {
            loginViewModel.logIn.onNext(Unit)
        }

        signUpButton.setOnClickListener {
            loginViewModel.signUp.onNext(Unit)
        }

        loginViewModel.logInErrorStatus
            .subscribe {
                if (it != null)
                    Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }

        loginViewModel.logInSuccessStatus
            .subscribe {
                Toast.makeText(this.context, "Success!", Toast.LENGTH_LONG).show()
            }

        emailTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (loginViewModel.emailText.value != s.toString().trim())
                    loginViewModel.emailText.onNext(s.toString().trim())
                //loginViewModel.passwordText = mEmailTextInput.text.toString().trim()
            }

        })

        loginViewModel.emailText
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                emailTextInput.setText(it.toCharArray(), 0, it.length)
                emailTextInput.setSelection(emailTextInput.text.toString().length)
            }

        loginViewModel.passwordText
            .distinctUntilChanged()
            .subscribe {
                passwordTextInput.setText(it.toCharArray(), 0, it.length)
                passwordTextInput.setSelection(passwordTextInput.text.toString().length)
            }

        passwordTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (loginViewModel.passwordText.value != s.toString().trim())
                    loginViewModel.passwordText.onNext(s.toString().trim())
            }

        })

    }

    override fun onDestroy() {
        Log.d("Fragment","onDestroy")
        super.onDestroy()
    }

    override fun onPause() {
        Log.d("Fragment","onPause")
        loginViewModel.emailText.onComplete()
        loginViewModel.passwordText.onComplete()
        loginViewModel.logIn.onComplete()
        loginViewModel.canLogIn.onComplete()
        loginViewModel.emailValid.onComplete()
        loginViewModel.passwordValid.onComplete()
        super.onPause()
    }

    override fun onAttach(context: Context) {
        Log.d("Fragment","onAttach")
        super.onAttach(context)
    }

    override fun onStop() {
        Log.d("Fragment","onStop")
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        Log.d("Fragment","onStart")
    }

    companion object {
        @JvmStatic
        fun newInstance() : LoginFragment {
            val fragment = LoginFragment()
            return fragment
        }
    }

}