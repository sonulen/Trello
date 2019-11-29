package com.example.trello.ViewModels.TrelloAuthService

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import javax.inject.Inject

class TrelloAuthServiceViewModelFactory
@Inject constructor(private val authService: TrelloAuthService) :
        ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return TrelloAuthServiceViewModel(authService) as T
    }
}