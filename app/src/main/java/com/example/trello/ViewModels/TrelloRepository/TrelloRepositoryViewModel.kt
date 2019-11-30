package com.example.trello.ViewModels.TrelloRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.trello.Network.TrelloClient.TrelloClient
import com.example.trello.Data.RepostirotyState
import com.example.trello.Data.TrelloRepository
import com.example.trello.TrelloBoards.HomePage.BoardTouchHelperAdapter


class TrelloRepositoryViewModel
    constructor(private val client: TrelloClient): ViewModel() {

    private val repository = TrelloRepository(client)
    private var cacheList = mutableListOf<TrelloRepository.Item>()
    private var representList = Transformations.map(repository.getState()) {
        if (it == RepostirotyState.UPDATED) {
            cacheList = repository.dataForPresent
        }
        cacheList
    }

    /**
     * Методы для доступа к данным
     */
    fun getList(): LiveData<MutableList<TrelloRepository.Item>>  = representList

    /**
     * Доступ в LiveData состояния Repository, для отображения ошибок
     */
    fun getState() = repository.getState()

    /**
     * Функция генерирует список организаций
     */
    fun getOrgList() : List<String> {
        val s: MutableList<String> = mutableListOf()
        for ((_, org) in repository.organizations) {
            s.add(org.name)
        }
        return s
    }

    /**
     * Методы для управления Model через ViewModel
     */
    fun addBoard(boardName: String, orgName: String) {
        repository.addBoard(boardName, orgName)
    }

    fun boardMove(from: TrelloRepository.BoardItem,
                  to: TrelloRepository.BoardItem,
                  fromPosition: Int,
                  toPosition: Int): Boolean {
        return repository.boardMove(from,to,fromPosition,toPosition)
    }

    fun itemDismiss(itemList: MutableList<TrelloRepository.Item>,
                    item: TrelloRepository.Item,
                    myBoardsRecyclerViewAdapter: BoardTouchHelperAdapter
    ) {
        repository.itemDismiss(itemList,item,myBoardsRecyclerViewAdapter)
    }

    fun refresh() {
        repository.updateAllData()
    }
}