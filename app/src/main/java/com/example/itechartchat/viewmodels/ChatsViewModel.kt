package com.example.itechartchat.viewmodels

import com.example.itechartchat.coordinators.LoginFlowCoordinatorInterface
import com.example.itechartchat.other.FirebaseAPIClient

interface ChatsViewModelInterface {

}

class ChatsViewModel (val coodinator: LoginFlowCoordinatorInterface): ChatsViewModelInterface, FirebaseAPIClient {



}