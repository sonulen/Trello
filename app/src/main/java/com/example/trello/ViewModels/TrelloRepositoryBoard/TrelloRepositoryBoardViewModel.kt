package com.example.trello.ViewModels.TrelloRepositoryBoard

import androidx.lifecycle.ViewModel
import com.example.trello.Data.Board
import com.example.trello.Data.BoardInteraction
import com.example.trello.Data.Card

class TrelloRepositoryBoardViewModel
constructor(private val board: Board, private val client: BoardInteraction): ViewModel() {
    fun getBoardName(): String = board.name
    fun getBoardColumns() = board.lists
    fun addCard(columnId: String, nameCard: String) = client.addcard(board.id, columnId, nameCard)
    fun moveCard(card: Card, toColumn: Int, toRow: Int) {
        client.moveCard(card, toColumn, toRow)
    }

    fun changeColumPos(oldPosition: Int, newPosition: Int) {
        client.moveColum(board.id, oldPosition, newPosition)
    }
}