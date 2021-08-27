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
    val backAction: PublishSubject<Unit>

    val emailText: BehaviorSubject<String>
    val emailValid: BehaviorSubject<Boolean>
    val passwordText: BehaviorSubject<String>
    val passwordValid: BehaviorSubject<Boolean>
    val confirmedPasswordText: BehaviorSubject<String>
    val confirmedPasswordValid: BehaviorSubject<Boolean>

    val canSignUp: BehaviorSubject<Boolean>
    val canConfirm: BehaviorSubject<Boolean>

    val isConfirmed: BehaviorSubject<Boolean>

    val signUp: PublishSubject<Unit>

    val recoverableError: PublishSubject<String?>
}

@SuppressLint("CheckResult")
class SignUpViewModel(private val coordinator: SignUpFlowCoordinatorInterface) :
    SignUpViewModelInterface, FirebaseAPIClient {
    override val backAction = PublishSubject.create<Unit>()

    override val emailText = BehaviorSubject.createDefault("unstopav@gmail.com")
    override val emailValid = BehaviorSubject.create<Boolean>()
    override val passwordText = BehaviorSubject.createDefault("qwerty12345")
    override val passwordValid = BehaviorSubject.create<Boolean>()
    override val confirmedPasswordText = BehaviorSubject.create<String>()
    override val confirmedPasswordValid = BehaviorSubject.create<Boolean>()

    override val canSignUp = BehaviorSubject.createDefault(false)
    override val canConfirm = BehaviorSubject.createDefault(false)
    override val isConfirmed = BehaviorSubject.createDefault(false)

    override val signUp = PublishSubject.create<Unit>()

    override val recoverableError = PublishSubject.create<String?>()

    init {
        backAction
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

        val emailValidated = emailTextValidate
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

        val passwordValidated = passwordTextValidate
            .filter { it.first }
            .map { it.second }

        passwordTextValidate
            .subscribe {
                passwordValid.onNext(it.first)
            }

        val confirmedPasswordValidate = Observable.combineLatest(passwordTextValidate,
            confirmedPasswordText
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged(), { first, second ->
                Pair(first, second)
            })
            .map { Pair(it.first.second == it.second, it.second) }
            .cacheWithInitialCapacity(1)

        confirmedPasswordValidate
            .subscribe {
                isConfirmed.onNext(it.first)
            }

        Observable.combineLatest(
            emailTextValidate, passwordTextValidate, { first, second ->
                Pair(first, second)
            })
            .map {
                it.first.first && it.second.first
            }
            .subscribe {
                canConfirm.onNext(it)
            }

        Observable
            .combineLatest(
                emailTextValidate,
                passwordTextValidate,
                confirmedPasswordValidate,
                { first, second, third ->
                    first.first && second.first && third.first
                })
            .subscribe {
                canSignUp.onNext(it)
            }

        signUp
            .withLatestFrom(
                Observable.combineLatest(
                    emailValidated,
                    passwordValidated,
                    { first, second -> Pair(first, second) }),
                { first, second -> second }
            )
            .observeOn(Schedulers.newThread())
            .flatMapSingle {
                register(it.first, it.second)
            }
            .doOnError {
                recoverableError.onNext(it.localizedMessage)
            }
            .retry()
            .observeOn(Schedulers.newThread())
            .subscribe {
                recoverableError.onNext("Success!")
            }

    }
}