package com.example.itechartchat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */

class LoginFragment : Fragment() {

    private lateinit var mBinding: LoginFragment
    private lateinit var mEmailTextInput : TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        mEmailTextInput = view.findViewById(R.id.email_textinputedittext)
        //isValidEditText(mEmailTextInput.text.toString().trim())
        // Inflate the layout for this fragment
        return view
    }

    override fun onResume() {
        var isTextChanged = false
        mEmailTextInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.toString().length == 0 && isTextChanged == true) {
                    mEmailTextInput.error = "Email is required!"
                    mEmailTextInput.requestFocus()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                
            }

            override fun afterTextChanged(s: Editable?) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s.toString().trim()).matches()) {
                    mEmailTextInput.setError("Your email is invalid!")
                    mEmailTextInput.requestFocus()
                }
                isTextChanged = true
            }
        })
        //isValidEditText(mEmailTextInput.text.toString().trim())
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() : LoginFragment {
            val fragment = LoginFragment()
            return fragment
        }
    }

}