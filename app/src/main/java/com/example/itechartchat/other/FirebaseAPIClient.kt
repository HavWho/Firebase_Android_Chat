package com.example.itechartchat.other

import android.util.Patterns
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Single
import io.reactivex.SingleSource

interface FirebaseAPIClient {

    fun authenticate(email: String, password: String): SingleSource<AuthResult>? {
        val auth = FirebaseAuth.getInstance()
        return Single.create{ emitter ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(it.result)
                    }
                    else
                        emitter.onError(it.exception)
                }
        }
    }

    fun register(email: String, password: String): SingleSource<AuthResult>? {
        val auth = FirebaseAuth.getInstance()
        return Single.create{ emitter ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(it.result)
                    }
                    else
                        emitter.onError(it.exception)
                }
        }
    }

    fun passwordValidate(value: String) : Pair<Boolean, String> {
        return Pair(value.length >= 6, value)
    }

    fun emailValidate(value: String) : Pair<Boolean, String> {
        return Pair(Patterns.EMAIL_ADDRESS.matcher(value).matches(), value)
    }

}