package com.example.itechartchat.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import com.example.itechartchat.R
import com.example.itechartchat.viewmodels.SignUpViewModelInterface
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers

class SignUpFragment(val viewModel: SignUpViewModelInterface) : Fragment() {

    private lateinit var confirmPasswordTextInput: TextInputEditText
    private lateinit var signUpButton: AppCompatButton
    private lateinit var emailTextInput: TextInputEditText
    private lateinit var passwordTextInput: TextInputEditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        confirmPasswordTextInput = view.findViewById(R.id.repeat_password_textinputedittext)
        signUpButton = view.findViewById(R.id.signup_button)
        emailTextInput = view.findViewById(R.id.email_signup_textinputedittext)
        passwordTextInput = view.findViewById(R.id.password_signup_textinputedittext)
        return view
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()

        emailTextInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(viewModel.emailText.value != s.toString().trim())
                    viewModel.emailText.onNext(s.toString().trim())
            }

        })

        passwordTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (viewModel.passwordText.value != s.toString().trim())
                    viewModel.passwordText.onNext(s.toString().trim())
            }
        })

        confirmPasswordTextInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (viewModel.confirmedPasswordText.value != s.toString().trim())
                    viewModel.confirmedPasswordText.onNext(s.toString().trim())
            }

        })

        viewModel.emailValid
            .filter {
                it.equals(false)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                emailTextInput.setError(getString(R.string.emailError), null)
            }

        viewModel.passwordValid
            .filter {
                it.equals(false)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                passwordTextInput.setError(getString(R.string.passwordError), null)
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.backAction.onNext(Unit)
                }
            })

        viewModel.isConfirmed
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it != true)
                    confirmPasswordTextInput.setError(getString(R.string.password_signup_mismatch), null)
            }

        viewModel.canSignUp
            .subscribe {
                Log.d("IsClickable", it.toString())
                signUpButton.isClickable = it
            }

        viewModel.canSignUp
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                if (it) 1.0f else 0.5f
            }
            .subscribe {
                signUpButton.alpha = it
            }

        signUpButton.setOnClickListener {
            viewModel.signUp.onNext(Unit)
        }

        viewModel.recoverableError
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this.context, it, Toast.LENGTH_LONG).show()
            }

        viewModel.canConfirm
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                confirmPasswordTextInput.isEnabled = it
            }

        viewModel.canConfirm
            .map {
                if (it) 1.0f else 0.5f
            }
            .subscribe {
                confirmPasswordTextInput.alpha = it
            }

        viewModel.emailText
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                emailTextInput.setText(it.toCharArray(), 0, it.length)
                emailTextInput.setSelection(emailTextInput.text.toString().length)
            }

        viewModel.passwordText
            .distinctUntilChanged()
            .subscribe {
                passwordTextInput.setText(it.toCharArray(), 0, it.length)
                passwordTextInput.setSelection(passwordTextInput.text.toString().length)
            }

    }

}
