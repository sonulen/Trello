package com.example.trello.Network.TrelloClient

import com.example.trello.Network.TrelloService.CardData
import io.reactivex.Completable
import io.reactivex.Single

interface CardInteraction {
    fun loadCardsListOfBoard(idBoard : String): Single<Array<CardData>>
    fun postCreateCard (name: String, idList: String, pos: String = "top"): Completable
    fun updateCard(idCard : String, newIdList : String, pos : String = "top"): Completable
    fun deleteCard(idCard: String): Completable
}