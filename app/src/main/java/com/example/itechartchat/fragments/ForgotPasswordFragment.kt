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
import com.example.itechartchat.viewmodels.ForgotPasswordViewModel
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordFragment(val viewModel: ForgotPasswordViewModel) : Fragment() {

    private lateinit var emailTextInput: TextInputEditText
    private lateinit var recoverPassword: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        emailTextInput = view.findViewById(R.id.email_recover_password_textinputedittext)
        recoverPassword = view.findViewById(R.id.recover_button)
        return view
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.backAction.onNext(Unit)
                }
            })

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

        viewModel.recoverableError
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this.context, it, Toast.LENGTH_LONG).show()
            }

        viewModel.emailValid
            .filter {
                !it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("emailValidate", it.toString())
                emailTextInput.setError(getString(R.string.emailError), null)
            }

        viewModel.canRestore
            .subscribe {
                Log.d("IsClickable", it.toString())
                recoverPassword.isClickable = it
            }

        viewModel.canRestore
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                if (it) 1.0f else 0.5f
            }
            .subscribe {
                recoverPassword.alpha = it
            }

        viewModel.emailText
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                emailTextInput.setText(it.toCharArray(), 0, it.length)
                emailTextInput.setSelection(emailTextInput.text.toString().length)
            }

        recoverPassword.setOnClickListener {
            viewModel.restore.onNext(Unit)
        }


    }

}