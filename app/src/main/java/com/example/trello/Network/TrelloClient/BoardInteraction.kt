package com.example.trello.Network.TrelloClient

import com.example.trello.Network.TrelloService.BoardData
import io.reactivex.Completable
import io.reactivex.Single

interface BoardInteraction {
    fun loadBoardList(id : String = "me"): Single<Array<BoardData>>
    fun postCreateBoard (name : String, idOrganization: String): Completable
    fun deleteBoard(idBoard : String): Completable
}
