package com.example.trello.ViewModels.TrelloAuthService

import androidx.lifecycle.ViewModel
import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import javax.inject.Inject

class TrelloAuthServiceViewModel
    constructor(authService: TrelloAuthService) : ViewModel() {
    private val trelloAuthService: TrelloAuthService = authService

}