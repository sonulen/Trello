package com.example.trello.ViewModels.TrelloRepositoryBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trello.Data.Board
import com.example.trello.Data.BoardInteraction

class TrelloRepositoryBoardViewModelFactory
constructor(private val board: Board, private val client: BoardInteraction) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return TrelloRepositoryBoardViewModel(board, client) as T
    }
}