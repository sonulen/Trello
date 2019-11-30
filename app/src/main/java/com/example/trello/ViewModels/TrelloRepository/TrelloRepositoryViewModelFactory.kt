package com.example.trello.ViewModels.TrelloRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trello.Network.TrelloClient.TrelloClient

class TrelloRepositoryViewModelFactory
constructor(private val client: TrelloClient) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return TrelloRepositoryViewModel(client) as T
    }
}