package com.example.trello.Data

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.trello.Network.TrelloClient.TrelloClient
import com.example.trello.Network.TrelloService.BoardData
import com.example.trello.Network.TrelloService.CardData
import com.example.trello.Network.TrelloService.ListData
import com.example.trello.Network.TrelloService.OrganizationData
import com.example.trello.TrelloBoards.HomePage.BoardTouchHelperAdapter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


enum class RepostirotyState {
    NOT_INITED,
    LOADING,
    UPDATED,
    FAILED
}
/**
    Класс хранилище всех досок созданных и данных внутри них
 */
class TrelloRepository(private val client: TrelloClient): OrganizationInteraction {

    /// Текущее состояние репозитория
    private var state = MutableLiveData<RepostirotyState>(RepostirotyState.NOT_INITED)

    /**
     * Все данные полученные из Trello
     * Ввиде вложенных классов:
     * Organization -> List -> Board -> Card
     */
    val organizations = hashMapOf<String, Organization>()

    /**
     * Данные ввиде списка для представления в RecyclerView
     */
    var dataForPresent = mutableListOf<Item>()

    /**
     * Выполняет следующие действия:
     * 1. Загружает списко какой есть на тек. момент - Пустой
     * 2. Инциализирует загрузку всех данных
     */
    init {
        // С генерируем пустой пока список
        updatePresentList()
        // Начнём загрузку всех данных
        requestAllData()
    }

    /**
     * Функция которая подписывается на обновление списка локально
     * и пробрасывает его на хендлер
     */
    @SuppressLint("CheckResult")
    fun updatePresentList() {
        //Сами сгенерим поток с Single списом и подпишемся
        // Когда список перегенерится обновим по хендлеру его на UI
        generateNewList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                dataForPresent = list
                state.value = RepostirotyState.UPDATED
            }
    }

    /**
     * Функция которая генерирует источник с новым списком Организаций - Досок
     */
    fun generateNewList() : Single<MutableList<Item>> {
        var representList = mutableListOf<Item>()

        for ((key, org) in organizations) {
            // Добавим категорию
            representList.add(Item(Item.TYPE.ORGANIZATION, org.id, org.name))
            // Добавим доски из категории
            if (org.boards.count() != 0) {
                // Если в орг есть доски - добавим их
                var sorted_boards = org.boards.values.sortedBy { it.seq }

                for (board in sorted_boards) {
                    representList.add(BoardItem(Item.TYPE.BOARD, board.id, board.idOrganization, board.name))
                }

            } else {
                // Если в категории нет досок добавим пустую для сообщения
                representList.add(EmptyBoardItem(Item.TYPE.EMPTY_BOARD, "nope", key, "This organization havent boards yet"))
            }
        }

        if (representList.isEmpty()) {
            representList.add(Item(Item.TYPE.FINALY_EMPTY, "nope","It's completely empty here"))
        }

        return Single.just(representList)
    }

    ///////////////////////// Функции для запроса данных /////////////////////////////////
    /**
     * Функция инициализирующая загрузку всех данных с Trello
     */
    @SuppressLint("CheckResult")
    override fun requestAllData() {
        state.value = RepostirotyState.LOADING
        // Грузим данные от trello. Начнём с организаций
        client.loadOrganization()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { orgList -> updateOrganizations(orgList)},
                { _ -> state.value = RepostirotyState.FAILED }
            )
    }

    /**
     * Функция запроса всех досок и подписка на поток
     * для обновления
     */
    @SuppressLint("CheckResult")
    override fun requestBoards() {
        state.value = RepostirotyState.LOADING

        client.loadBoardList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                { boardList -> updateBoards(boardList) },
                { _ -> state.value = RepostirotyState.FAILED }
            )
    }

    /**
     * Функция запроса колонок для доски
     */
    @SuppressLint("CheckResult")
    override fun requestColumns(idBoard: String) {
        client.loadListOfBoard(idBoard)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lists -> updateLists(idBoard, lists)},
                { _ -> state.value = RepostirotyState.FAILED }
            )
    }

    /**
     * Функция запроса всех карточек для доски
     * и подписка на их поток для обновления
     */
    @SuppressLint("CheckResult")
    override fun requestCards(idBoard: String) {
        state.value = RepostirotyState.LOADING

        // Загрузим для доски все карточки теперь и распихаем по столбцам
        client.loadCardsListOfBoard(idBoard)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { cards -> updateCards(idBoard, cards) },
                { _ -> state.value = RepostirotyState.FAILED }
            )
    }

    /////////////////////// Функции для обработки полученных данных ///////////////////////////////
    /**
     * Функция обработки ответа от апи с списком организаций
     */
    fun updateOrganizations(answer : Array<OrganizationData>) {
        // Добавим Personal организацию если ее еще нет
        if (!organizations.containsKey("null")) {
            organizations["null"] = Organization("null", "Personal")
        }

        for (org in answer) {
            // Добавим все которые пришли если их еще нет
            if (!organizations.containsKey(org.id)) {
                var new_org = Organization(org.id ?: "null", org.displayName ?: "Personal")
                organizations[new_org.id] = new_org
            }
        }

        // Загрузим Boards
        requestBoards()
    }

    /**
     * Функция обработки ответа от апи с списком досок
     */
    @SuppressLint("CheckResult")
    fun updateBoards(answer : Array<BoardData>) {
        // Распарсим ответ с досками
        for (board in answer) {
            if (!organizations.containsKey(board.idOrganization ?: "null")) {
                continue
            }
            var org = organizations[board.idOrganization ?: "null"]!!

            // Если такой доски еще нет до добавим
            if (!org.boards.containsKey(board.id)) {
                var new_board = Board(board.id ?: "0", org.id,
                    board.name ?: "oops", org.boards.count() + 1)
                org.add_board(new_board)
            }
        }

        for ((_, org) in organizations) {
            for ((_,board) in org.boards) {
                // Для каждой доски загрузим список столбцов
                requestColumns(board.id)
            }
        }

        // Для RecyclerView данные уже готовы - обновим список
        updatePresentList()
    }

    /**
     * Функция обработки ответа от апи с списком колонок
     */
    fun updateLists(idBoard: String, answer : Array<ListData>) {
        var uBoard : Board? = null

        // Найдем доску к которой относится лист
        for ((_, org) in organizations) {
            for ((_, board) in org.boards) {
                if (board.id == idBoard) {
                    uBoard = board
                    break
                }
            }
        }

        uBoard?.let {
            for (list in answer) {
                // Добавим колонку если ее еще нет
                if (!uBoard.lists.containsKey(list.id)) {
                    var new_list = List(
                        list.id ?: "oops",
                        it.id,
                        list.name ?: "oops",
                        uBoard.lists.count() + 1
                    )
                    uBoard.add_list(new_list)
                }
            }

            // Загрузим для доски все карточки теперь и распихаем по столбцам
            requestCards(idBoard)
        }
    }

    /**
     * Функция обрабочик поступившего списка кароточек
     * Добавляет карточки к доске idBoard
     */
    fun updateCards(idBoard: String, answer: Array<CardData>) {
        var uBoard : Board? = null

        // Найдем доску которую будем обновлять
        for ((_, org) in organizations) {
            for ((_, board) in org.boards) {
                if (board.id == idBoard) {
                    uBoard = board
                    break
                }
            }
        }

        // Добавим карточку если есть столбец которому она принадлежит
        // И если ее еще нет.
        uBoard?.let {
            for (card in answer) {
                if (uBoard.lists.containsKey(card.idList) &&
                    !uBoard.lists[card.idList]!!.cards.containsKey(card.id)) {
                    // Temp новая карточка
                    var new_card = Card(card.id ?: "oops", card.idList ?: "oops",
                        card.name ?: "oops", uBoard.lists[card.idList]!!.cards.count() + 1)
                    // Заносим карточку в доску
                    uBoard.lists[new_card.idList]!!.add_card(new_card)
                }
            }
        }
    }

    /**
     * Данные для подписки - Состояние Repository
     */
    fun getState() : LiveData<RepostirotyState> = state


    //////////////////// Функции для взаимодействия с Repository из вне ///////////////////////
    // Метод добавления доски.
    @SuppressLint("CheckResult")
    override fun addBoard(name : String, org_name: String) {
        // Добавление доски в категорию выбранную
        var org_id = organizations.filterValues { it.name == org_name }.keys.first()

        // Создадим на самом трело доску
        // Но так как на trello организация Person = "" надо проверить
        if (org_id == "null") {
            org_id = ""
        }

        // Добавим доску на сервере
        client.postCreateBoard(name, org_id)
            .subscribeOn(Schedulers.io())
            .subscribe {
                requestBoards()
            }
    }

    @SuppressLint("CheckResult")
    override fun addcard(idBoard: String, columnId: String, nameCard: String) {
        // Добавим карточку
        // И если запрос будет выполнен успешно обновим репозиторий
        client.postCreateCard(nameCard, columnId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                requestCards(idBoard)
            }
    }

    fun boardMove(
        from: BoardItem,
        to: BoardItem,
        fromPosition: Int,
        toPosition: Int
    ): Boolean {

        if (from.idOrganization != to.idOrganization) {
            return false
        }

        if (!organizations.containsKey(from.idOrganization)) {
            return false
        }

        if (toPosition - fromPosition == 0) {
            return false
        }

        // Сдвинули все элементы после вставляемого
        for ((_, board) in organizations[from.idOrganization]!!.boards) {
            if (board.seq >= toPosition && board.seq != fromPosition) {
                board.seq += 1
            }
        }

        // Сдвинули сам элемент
        organizations[from.idOrganization]!!.boards[from.id]!!.seq = toPosition

        return true
    }

    fun itemDismiss(
        itemList: MutableList<Item>,
        item: Item,
        myBoardsRecyclerViewAdapter: BoardTouchHelperAdapter
    ) {
        // Удаление доски из Организации
        when (item.type) {
            Item.TYPE.EMPTY_BOARD -> {
                var eBItem = item as EmptyBoardItem
                // т.к. мы удаляем еще и Организацию, удалим ее из Адаптера
                var index = itemList.indexOf(itemList.find { it.type == Item.TYPE.ORGANIZATION
                        && it.id == eBItem.idOrganization })
                if (index != -1) {
                    myBoardsRecyclerViewAdapter.onItemDismiss(index)
                }
            }
            Item.TYPE.ORGANIZATION -> {
                // Удаляя организацию мы должны каждый его подэлемент удалить
                var listForRemove = mutableListOf<Int>()
                for (inner_item in itemList) {
                    when(inner_item) {
                        is EmptyBoardItem -> {
                            if (inner_item.idOrganization == item.id) {
                                listForRemove.add(itemList.indexOf(inner_item))
                            }
                        }
                        is BoardItem -> {
                            if (inner_item.idOrganization == item.id) {
                                listForRemove.add(itemList.indexOf(inner_item))
                            }
                        }
                    }
                }
                // Можем удалять только в обратном порядке иначе порядок их нарушится
                listForRemove.reverse()
                for (index in listForRemove) {
                    myBoardsRecyclerViewAdapter.onItemDismiss(index)
                }
                // Удалим организацию
                if (organizations.containsKey(item.id)) {
                    // Удалим с сервера
                    client.deleteOrganization(item.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            // Удалим организацию из мапы
                            organizations.remove(item.id)
                            updatePresentList()
                        }
                }
            }
            Item.TYPE.BOARD -> {
                var bItem = item as BoardItem
                // Удалим доску с сервера
                client.deleteBoard(bItem.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        // Удалим доску из мапы
                        organizations[bItem.idOrganization]?.boards?.remove(bItem.id)

                        if (organizations[bItem.idOrganization]?.boards?.count() == 0) {
                            // т.к. мы удаляем еще и Организацию, удалим ее из Адаптера и рекурсивно и у нас
                            var index = itemList.indexOf(itemList.find { it.type == Item.TYPE.ORGANIZATION
                                    && it.id == bItem.idOrganization })
                            if (index != -1) {
                                myBoardsRecyclerViewAdapter.onItemDismiss(index)
                            }
                        }
                        updatePresentList()
                    }
            }
            Item.TYPE.FINALY_EMPTY -> {}
        }
    }

    fun getBoard(idBoard: String): Board? {
        for ((_, org) in organizations) {
            if (org.boards.contains(idBoard)) {
                return org.boards[idBoard]
            }
        }
        return null
    }

    /**
     * Данные для отображения на RecyclerView
     */
    open class Item (val type : TYPE,
                     val id : String,
                     val name : String) {
        enum class TYPE (val type: Int) {
            ORGANIZATION(1),
            BOARD(2),
            EMPTY_BOARD(3),
            FINALY_EMPTY(4)
        }
    }

    class BoardItem (typeofBoard : TYPE,
                     id : String,
                     val idOrganization: String,
                     name : String) : Item(type = typeofBoard, id = id, name = name)

    class EmptyBoardItem (typeofBoard : TYPE,
                          id : String,
                          val idOrganization: String,
                          name : String) : Item(type = typeofBoard, id = id, name = name)
}
