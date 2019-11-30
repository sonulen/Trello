package com.example.trello.Data

interface BoardInteraction: ListInteraction {
    fun requestColumns(idBoard: String)
    fun requestCards(idBoard: String)
    fun addcard(idBoard: String, columnId: String, nameCard: String)
}
