package com.example.itechartchat

import android.annotation.SuppressLint
import android.util.Patterns
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.regex.Pattern

@SuppressLint("CheckResult")
class LoginViewModel {

    val emailText = BehaviorSubject.create<String>()
    val passwordText = BehaviorSubject.create<String>()
    val canLogIn = BehaviorSubject.create<Boolean>()
    val logIn = PublishSubject.create<Unit>()

    val scheduler = Schedulers.newThread()
    val worker = scheduler.createWorker()

    private fun passwordValidate(value: String) : Boolean {
        val PASSWORD_REGEX = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})"
        val pattern = Pattern.compile(PASSWORD_REGEX)
        val matcher = pattern.matcher(value)
        return !matcher.matches()
    }

    private fun emailValidate(value: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }

    init {
        emailText
            .mergeWith(passwordText)
            .map {
                emailValidate(it)
                passwordValidate(it)
            }
            .subscribe {
                canLogIn.onNext(it)
            }
    }

}