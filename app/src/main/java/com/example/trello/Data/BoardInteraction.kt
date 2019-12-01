package com.example.trello.Data

interface BoardInteraction: ListInteraction {
    fun requestColumns(idBoard: String)
    fun requestCards(idBoard: String)
    fun addcard(idBoard: String, columnId: String, nameCard: String)
    fun moveCard(card: Card, toColumn: Int, toRow: Int)
    fun moveColum(idBoard: String, oldPosition: Int, newPosition: Int)
    fun removeCard(board: Board, card: Card)
}
