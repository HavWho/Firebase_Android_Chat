package com.example.itechartchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */

class LoginFragment : Fragment() {

    private lateinit var mBinding: LoginFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() : LoginFragment {
            val fragment = LoginFragment()
            return fragment
        }
    }

}