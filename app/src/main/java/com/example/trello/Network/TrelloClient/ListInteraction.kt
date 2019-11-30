package com.example.trello.Network.TrelloClient

import com.example.trello.Network.TrelloService.ListData
import io.reactivex.Completable
import io.reactivex.Single

interface ListInteraction {
    fun loadListOfBoard(idBoard : String): Single<Array<ListData>>
    fun postCreateList (name: String, idBoard: String, pos: String = "top"): Completable
    fun archiveList(idList: String, state: String): Completable
}