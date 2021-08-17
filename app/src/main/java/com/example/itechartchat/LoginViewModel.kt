package com.example.itechartchat

import android.annotation.SuppressLint
import android.util.Log
import android.util.Patterns
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.SingleSource
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class LoginViewModel {

    val emailText = BehaviorSubject.createDefault("test")
    val passwordText = BehaviorSubject.create<String>()
    val canLogIn = BehaviorSubject.createDefault(false)
    val logIn = PublishSubject.create<Unit>()

    //val logInStatus = BehaviorSubject.create<>()

    private val auth = FirebaseAuth.getInstance()

    val emailValid = BehaviorSubject.create<Boolean>()
    val passwordValid = BehaviorSubject.create<Boolean>()

    private fun authenticate(email: String, password: String): SingleSource<AuthResult>? {
            return Single.create{ emitter ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        emitter.onSuccess(it.result)
                        emitter.onError(it.exception)
                    }
            }
    }

    private fun passwordValidate(value: String) : Pair<Boolean, String> {
        return Pair(value.length >= 6, value)
    }

    private fun emailValidate(value: String) : Pair<Boolean, String> {
        return Pair(Patterns.EMAIL_ADDRESS.matcher(value).matches(), value)
    }

    init {

        val emailTextSharedValidate = emailText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                emailValidate(it)
            }
            .share()
//            .cacheWithInitialCapacity(1)

        val validatedEmail = emailTextSharedValidate
            .filter { it.first }
            .map { it.second }

        val passwordTextSharedValidate = passwordText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                passwordValidate(it)
            }
            .share()
//            .cacheWithInitialCapacity(1)

        val validatedPassword = passwordTextSharedValidate
            .filter { it.first }
            .map { it.second }


        logIn
            .withLatestFrom(
                Observable.combineLatest(validatedEmail, validatedPassword, {first, second -> Pair(first, second)})
            , {first, second -> second}
            )
            .observeOn(Schedulers.newThread())
            .doOnNext {
                Log.d("doOnNextLogIn", it.toString())
            }
            .flatMapSingle {
                authenticate(it.first, it.second)
            }
            .subscribe {
                println(it.user?.uid)
            }

        passwordTextSharedValidate
            .doOnNext {
                Log.d("Error Signal", it.first.toString())
            }
            .subscribe {
                passwordValid.onNext(it.first)
            }

        emailTextSharedValidate
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
                Log.d("doOnNextCombined texts", it.toString())
            }
            .subscribe {
                canLogIn.onNext(it)
            }




    }

}