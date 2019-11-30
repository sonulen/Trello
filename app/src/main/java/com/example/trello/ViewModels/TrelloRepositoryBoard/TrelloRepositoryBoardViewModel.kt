package com.example.trello.ViewModels.TrelloRepositoryBoard

import androidx.lifecycle.ViewModel
import com.example.trello.Data.Board
import com.example.trello.Data.BoardInteraction

class TrelloRepositoryBoardViewModel
constructor(private val board: Board, private val client: BoardInteraction): ViewModel() {
    fun getBoardName(): String = board.name
    fun getBoardColumns() = board.lists
    fun addCard(columnId: String, nameCard: String) = client.addcard(board.id, columnId, nameCard)
}