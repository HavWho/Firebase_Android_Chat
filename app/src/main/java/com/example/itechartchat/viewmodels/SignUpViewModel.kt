package com.example.itechartchat.viewmodels

import android.annotation.SuppressLint
import com.example.itechartchat.coordinators.SignUpFlowCoordinatorInterface
import com.example.itechartchat.other.FirebaseAPIClient
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface SignUpViewModelInterface {
    val backButtonPressed: PublishSubject<Unit>

    val emailText: BehaviorSubject<String>
    val emailValid: BehaviorSubject<Boolean>
    val passwordText: BehaviorSubject<String>
    val passwordValid: BehaviorSubject<Boolean>

    val canSignUp: BehaviorSubject<Boolean>
    val canConfirm: BehaviorSubject<Boolean>

    val signUp: BehaviorSubject<Unit>

    val recoverableError: PublishSubject<String?>
}

@SuppressLint("CheckResult")
class SignUpViewModel(val coordinator: SignUpFlowCoordinatorInterface): SignUpViewModelInterface, FirebaseAPIClient {
    override val backButtonPressed = PublishSubject.create<Unit>()

    override val emailText = BehaviorSubject.createDefault("unstopav@gmail.com")
    override val emailValid = BehaviorSubject.create<Boolean>()
    override val passwordText = BehaviorSubject.createDefault("qwerty12345")
    override val passwordValid = BehaviorSubject.create<Boolean>()

    override val canSignUp = BehaviorSubject.createDefault(false)
    override val canConfirm = BehaviorSubject.createDefault(false)

    override val signUp = BehaviorSubject.create<Unit>()

    override val recoverableError = PublishSubject.create<String?>()

    init {
        backButtonPressed
            .subscribe {
                coordinator.finishSignUpFragment()
            }

        val emailTextValidate = emailText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                emailValidate(it)
            }
            .cacheWithInitialCapacity(1)

        val validatedEmail = emailTextValidate
            .filter { it.first }
            .map { it.second }

        emailTextValidate
            .subscribe {
                emailValid.onNext(it.first)
            }

        val passwordTextValidate = passwordText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                passwordValidate(it)
            }
            .cacheWithInitialCapacity(1)

        val validatedPassword = passwordTextValidate
            .filter { it.first }
            .map { it.second }

        passwordTextValidate
            .subscribe {
                passwordValid.onNext(it.first)
            }

        Observable
            .combineLatest(emailText, passwordText, {first, second -> Pair(first, second)})
            .map {
                emailValidate(it.first).first and passwordValidate(it.second).first
            }
            .subscribe {
                canConfirm.onNext(it)
            }

    }
}