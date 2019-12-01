package com.example.trello.Data

import com.example.trello.Network.TrelloService.CardFullData

data class Card (val id: String, var idList: String, var idBoard: String, val name: String, var seq: Int) {
    var data: CardFullData? = null

    fun saveFullData(cardFullData: CardFullData) {
        data = cardFullData
    }
}

data class List (val id: String, var idBoard: String, val name: String, var seq: Int) {
    var cards = hashMapOf<String, Card>()

    fun add_card (card: Card) {
        cards[card.id] = card
    }
}

data class Board (val id : String, val idOrganization : String, val name : String, var seq : Int) {
    var lists = hashMapOf<String, List>()

    fun add_list(list: List) {
        lists[list.id] = list
    }
}

data class Organization (val id : String, val name : String)  {
    var boards = hashMapOf<String, Board>()

    fun add_board(board: Board) {
        boards[board.id] = board
    }
}