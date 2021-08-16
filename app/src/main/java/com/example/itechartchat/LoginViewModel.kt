package com.example.itechartchat

import android.annotation.SuppressLint
import android.util.Log
import android.util.Patterns
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

@SuppressLint("CheckResult")
class LoginViewModel {

    val emailText = BehaviorSubject.createDefault("test")
    val passwordText = BehaviorSubject.create<String>()
    val canLogIn = BehaviorSubject.createDefault(false)
    val logIn = PublishSubject.create<Unit>()

    private val auth = FirebaseAuth.getInstance()

    val emailValid = BehaviorSubject.create<Boolean>()
    val passwordValid = BehaviorSubject.create<Boolean>()

    private fun authenticate(email: String, password: String): Observable<AuthResult> {
        return Observable.create { emitter ->
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    emitter.onNext(it.result)
                    emitter.onComplete()
                }
        }
    }

    private fun passwordValidate(value: String) : Pair<Boolean, String> {
        /*val PASSWORD_REGEX = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{6,})"
        val pattern = Pattern.compile(PASSWORD_REGEX)
        val matcher = pattern.matcher(value)
        return !matcher.matches()*/
        return Pair(value.length >= 6, value)
    }

    private fun emailValidate(value: String) : Pair<Boolean, String> {
        return Pair(Patterns.EMAIL_ADDRESS.matcher(value).matches(), value)
    }

    private fun logInButtonClicked(value : Unit): Pair<String, String> {
        return Pair(emailText.value.toString().trim(), passwordText.value.toString().trim())
    }
/*
    inline fun handleError() {

    }*/

    init {

        val x = emailText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                emailValidate(it)
            }
            .share()

        val validatedEmail = x
            .filter { it.first }
            .map { it.second }

        val y = passwordText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                passwordValidate(it)
            }
            .share()

        val validatedPassword = y
            .filter { it.first }
            .map { it.second }

        logIn
            .withLatestFrom(
                Observable.combineLatest(validatedEmail, validatedPassword, {first, second -> Pair(first, second)})
            , {first, second -> second}
            )
            .doOnNext {
                Log.d("HUY", it.toString())
            }
            .subscribe {
                authenticate(it.first, it.second)
            }

        y
            .doOnNext {
                Log.d("Error Signal", it.first.toString())
            }
            .subscribe {
                passwordValid.onNext(it.first)
            }

        x
            .doOnNext {
                Log.d("Error Signal", it.first.toString())
            }
            .subscribe {
                emailValid.onNext(it.first)
            }

        Observable
            .combineLatest(emailText, passwordText, {first, second -> Pair(first, second)})
            .map {
                emailValidate(it.first).first and passwordValidate(it.second).first
            }
            .doOnNext {
                Log.d("HUY", it.toString())
            }
            .subscribe {
                canLogIn.onNext(it)
            }




    }

}