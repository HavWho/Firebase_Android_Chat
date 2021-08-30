package com.example.itechartchat.other

import android.util.Patterns

class Validator {

    fun passwordValidate(value: String): Pair<Boolean, String> {
        return Pair(value.length >= 6, value)
    }

    fun emailValidate(value: String): Pair<Boolean, String> {
        return Pair(Patterns.EMAIL_ADDRESS.matcher(value).matches(), value)
    }

}