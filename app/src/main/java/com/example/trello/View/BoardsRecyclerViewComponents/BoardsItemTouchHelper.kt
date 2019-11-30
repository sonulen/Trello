package com.example.trello.TrelloBoards.HomePage

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.Data.TrelloRepository

interface BoardTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}
class BoardTouchHelperCallback(val adapter: BoardTouchHelperAdapter)
    : ItemTouchHelper.SimpleCallback(UP or DOWN, START or END) {

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        // Мы не можем перемещать если:
        // исходный тип - Пусто
        // исходный тип - Заголовок Организации
        // таргет тип - Заголовок организации
        if (source.itemViewType == TrelloRepository.Item.TYPE.EMPTY_BOARD.type ||
            source.itemViewType == TrelloRepository.Item.TYPE.ORGANIZATION.type ||
            target.itemViewType == TrelloRepository.Item.TYPE.ORGANIZATION.type) {
            return false
        }

        // Можем перемещать если:
        // Исходный тип - Доска
        // Таргет тип - Либо пусто либо Доска
        if (source.itemViewType == TrelloRepository.Item.TYPE.BOARD.type &&
            (target.itemViewType == TrelloRepository.Item.TYPE.EMPTY_BOARD.type ||
                    target.itemViewType == TrelloRepository.Item.TYPE.BOARD.type)) {
            return adapter.onItemMove(source.adapterPosition, target.adapterPosition)
        }

        return false
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

}