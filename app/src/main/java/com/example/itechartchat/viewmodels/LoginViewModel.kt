package com.example.itechartchat.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import android.util.Patterns
import com.example.itechartchat.coordinators.CoordinatorInterface
import com.example.itechartchat.coordinators.LoginFlowCoordinator
import com.example.itechartchat.coordinators.LoginFlowCoordinatorInterface
import com.example.itechartchat.other.FirebaseAPIClient
import com.example.itechartchat.other.Validator
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.mindorks.nybus.NYBus
import io.reactivex.Observable
import io.reactivex.SingleSource
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface LoginViewModelInterface {
    val emailText: BehaviorSubject<String>
    val passwordText: BehaviorSubject<String>
    val emailValid: BehaviorSubject<Boolean>
    val passwordValid: BehaviorSubject<Boolean>

    val canLogIn: BehaviorSubject<Boolean>

    val logIn: PublishSubject<Unit>
    val signUp: PublishSubject<Unit>
    val forgotPassword: PublishSubject<Unit>

    val recoverableError: PublishSubject<String?>

}

@SuppressLint("CheckResult")
class LoginViewModel(val coordinator: LoginFlowCoordinatorInterface) : LoginViewModelInterface,
    FirebaseAPIClient {

    override val emailText = BehaviorSubject.createDefault("26.01.yanvar@gmail.com")
    override val passwordText = BehaviorSubject.createDefault("260102Sasha")
    override val canLogIn = BehaviorSubject.createDefault(false)

    override val logIn = PublishSubject.create<Unit>()
    override val recoverableError = PublishSubject.create<String?>()

    override val signUp = PublishSubject.create<Unit>()
    override val forgotPassword = PublishSubject.create<Unit>()

    override val emailValid = BehaviorSubject.create<Boolean>()
    override val passwordValid = BehaviorSubject.create<Boolean>()

    val validator = Validator()

    init {

        forgotPassword
            .subscribe {
                coordinator.startForgotPasswordFragment()
            }

        signUp
            .subscribe {
                coordinator.startSignUpFragment()
            }

        val emailTextSharedValidate = emailText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                validator.emailValidate(it)
            }
            .cacheWithInitialCapacity(1)

        val validatedEmail = emailTextSharedValidate
            .filter { it.first }
            .map { it.second }

        val passwordTextSharedValidate = passwordText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                validator.passwordValidate(it)
            }
            .cacheWithInitialCapacity(1)

        val validatedPassword = passwordTextSharedValidate
            .filter { it.first }
            .map { it.second }

        logIn
            .withLatestFrom(
                Observable.combineLatest(
                    validatedEmail,
                    validatedPassword,
                    { first, second -> Pair(first, second) }), { first, second -> second }
            )
            .observeOn(Schedulers.newThread())
            .doOnNext {
                Log.d("doOnNextLogIn", it.toString())
            }
            .doOnError {
                Log.d("doOnErrorBeforeRetryWhen", it.message.toString())
            }
            .flatMapSingle {
                authenticate(it.first, it.second)
            }
            .doOnError {
                recoverableError.onNext(it.localizedMessage)
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                recoverableError.onNext("Success")
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
            .combineLatest(emailText, passwordText, { first, second -> Pair(first, second) })
            .map {
                validator.emailValidate(it.first).first and validator.passwordValidate(it.second).first
            }
            .doOnNext {
                Log.d("doOnNextCombined texts", it.toString())
            }
            .subscribe {
                canLogIn.onNext(it)
            }

    }

}