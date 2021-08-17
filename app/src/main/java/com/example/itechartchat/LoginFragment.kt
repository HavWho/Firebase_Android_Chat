package com.example.itechartchat

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
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.internal.disposables.DisposableHelper.dispose
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */

class LoginFragment : Fragment() {

    private lateinit var mBinding: LoginFragment
    private lateinit var mEmailTextInput : TextInputEditText
    private lateinit var passwordTextInput : TextInputEditText
    private lateinit var loginButton : Button
    private lateinit var emailErrorText : String
    private val loginViewModel = LoginViewModel()

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        mEmailTextInput = view.findViewById(R.id.email_textinputedittext)
        passwordTextInput = view.findViewById(R.id.password_textinputedittext)
        loginButton = view.findViewById(R.id.login_button)
        //
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
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                if (it) 1.0f else 0.5f
            }
            .subscribe {
                loginButton.alpha = it
            }

        loginViewModel.emailValid
            .filter {
                it.equals(false)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mEmailTextInput.setError(getString(R.string.emailError), null)
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



        mEmailTextInput.addTextChangedListener(object : TextWatcher {
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
                mEmailTextInput.setText(it.toCharArray(), 0, it.length)
                mEmailTextInput.setSelection(mEmailTextInput.text.toString().length)
            }

        loginViewModel.passwordText
            .distinctUntilChanged()
            .subscribe {

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