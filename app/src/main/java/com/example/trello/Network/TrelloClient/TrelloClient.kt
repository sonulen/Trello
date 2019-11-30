package com.example.trello.Network.TrelloClient

import com.example.trello.Network.TrelloService.*
import io.reactivex.Completable
import io.reactivex.Single

class TrelloClient(private val service: TrelloService): 
    OrganizationInteraction,
    BoardInteraction,
    ListInteraction,
    CardInteraction {

    var RETRY_COUNT: Long = 10

    /// GET ///
    // Загружаем все команды
    override fun loadOrganization(id: String): Single<Array<OrganizationData>> {
        return service.getOrganizations(id = id)
            .retry(RETRY_COUNT)
    }

    // Загружаем все доски
    override fun loadBoardList(id : String): Single<Array<BoardData>> {
        return service.getBoards(id = id)
            .retry(RETRY_COUNT)
    }

    // Загружаем все колонки одной доски
    override fun loadListOfBoard(idBoard : String): Single<Array<ListData>> {
        return service.getLists(idBoard = idBoard)
            .retry(RETRY_COUNT)
    }

    // Загружаем все карточки одной доски
    override fun loadCardsListOfBoard(idBoard : String): Single<Array<CardData>> {
        return service.getCards(idBoard = idBoard)
            .retry(RETRY_COUNT)
    }

    /// POST ///
    // Создаем доску
    override fun postCreateBoard (name : String, idOrganization: String): Completable {
        return service.createBoard(name = name,
            idOrganization = idOrganization)
            .retry(RETRY_COUNT)
    }

    // Создаем список в доске
    override fun postCreateList (name: String, idBoard: String, pos: String): Completable {
        return service.createList(name = name, idBoard = idBoard)
            .retry(RETRY_COUNT)
    }

    // Создаем карточку в списке
    override fun postCreateCard (name: String, idList: String, pos: String): Completable {
        return service.createCard(name = name, idList = idList)
            .retry(RETRY_COUNT)
    }

    /// PUT ///
    // Обновим родительский список у карточки
    override fun updateCard(idCard : String, newIdList : String, pos : String): Completable {
        return service.updateCard(idCard = idCard, idList = newIdList)
            .retry(RETRY_COUNT)
    }

    // Заархивируем список
    override fun archiveList(idList: String, state: String): Completable {
        return service.archiveList(idList = idList, value = state)
            .retry(RETRY_COUNT)
    }

    /// DELETE ///
    // Удаление организации
    override fun deleteOrganization(idOrganization : String): Completable {
        return service.deleteOrganization(idOrganization = idOrganization)
            .retry(RETRY_COUNT)
    }

    // Удаление доски
    override fun deleteBoard(idBoard : String): Completable {
        return service.deleteBoard(idBoard = idBoard)
            .retry(RETRY_COUNT)
    }

    // Удаление карточки
    override fun deleteCard(idCard: String): Completable {
        return service.deleteCard(idCard = idCard)
            .retry(RETRY_COUNT)
    }
}