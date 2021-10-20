package com.example.itechartchat.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import com.example.itechartchat.coordinators.ForgotPasswordFlowCoordinatorInterface
import com.example.itechartchat.other.FirebaseAPIClient
import com.example.itechartchat.other.Validator
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface ForgotPasswordViewModelInterface {
    val backAction: PublishSubject<Unit>

    val emailText: BehaviorSubject<String>
    val emailValid: BehaviorSubject<Boolean>

    val canRestore: BehaviorSubject<Boolean>
    val restore: BehaviorSubject<Unit>

    val recoverableError: PublishSubject<String?>
}

@SuppressLint("CheckResult")
class ForgotPasswordViewModel(val coordinator: ForgotPasswordFlowCoordinatorInterface):
    ForgotPasswordViewModelInterface, FirebaseAPIClient {

    override val backAction = PublishSubject.create<Unit>()
    override val emailText = BehaviorSubject.createDefault("26.01.yanvar@gmail.com")
    override val emailValid = BehaviorSubject.create<Boolean>()
    override val canRestore = BehaviorSubject.createDefault(false)
    override val restore = BehaviorSubject.create<Unit>()
    override val recoverableError = PublishSubject.create<String?>()

    val validator = Validator()

    init {
        backAction
            .subscribe {
                coordinator.finishForgotPasswordFragment()
            }

        val emailValidate = emailText
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .map {
                validator.emailValidate(it)
            }

        emailValidate
            .subscribe {
                emailValid.onNext(it.first)
            }

        emailValidate
            .subscribe {
                canRestore.onNext(it.first)
            }

        restore
            .withLatestFrom(emailValidate, {first, second -> second})
            .observeOn(Schedulers.newThread())
            .flatMapSingle {
                recoverPassword(it.second)
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