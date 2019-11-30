package com.example.trello.TrelloBoards.HomePage

import androidx.recyclerview.widget.DiffUtil
import com.example.trello.Data.TrelloRepository

class OrganizationDiffUtilCallback(private val oldList: List<TrelloRepository.Item>, private val newList: List<TrelloRepository.Item>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.count()
    override fun getNewListSize() = newList.count()

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) : Boolean = oldList[oldItemPosition].javaClass == newList[newItemPosition].javaClass

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) : Boolean {
        if (oldList[oldItemPosition].type == newList[newItemPosition].type &&
                    oldList[oldItemPosition].id == newList[newItemPosition].id) {
            return true
        }
        return false
    }
}