package com.example.trello.TrelloBoards.HomePage

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.R
import com.example.trello.ViewModels.TrelloRepository.TrelloRepositoryViewModel
import com.example.trello.Data.TrelloRepository
import com.example.trello.Fragments.Boards.onBoardSelectionListener
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.empty_boards_list.view.*
import kotlinx.android.synthetic.main.header_boards_list.view.*
import kotlinx.android.synthetic.main.item_boards_list.view.*
import java.util.*

/**
 * Адаптер к TrelloRepository
 */
class MyBoardsRecyclerViewAdapter(
    private val repostitoryViewModel: TrelloRepositoryViewModel,
    private val onBoardSelectionListener: onBoardSelectionListener,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    BoardTouchHelperAdapter {

    override fun getItemViewType(position: Int): Int {
        var item = GetItem(position)
        return item.type.type
    }


    // toMutableList здесь нужен для того чтобы создать копию того списка. А не ссылаться на один и тот же
    var items = mutableListOf<TrelloRepository.Item>()
        @SuppressLint("CheckResult")
        set(value) {
            Single.just(value)
                .map {
                    val callback = OrganizationDiffUtilCallback(
                        field,
                        it
                    )
                    DiffUtil.calculateDiff(callback)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ diff ->
                    field = value.toMutableList()
                    diff.dispatchUpdatesTo(this)
                }
        }

    init {
        // Подпишимся на изменения viewModel
        repostitoryViewModel.getList().observe(activity, androidx.lifecycle.Observer {
            items = it
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TrelloRepository.Item.TYPE.ORGANIZATION.type -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_boards_list, parent, false)
                OrganizationViewHolder(
                    view
                )
            }
            TrelloRepository.Item.TYPE.EMPTY_BOARD.type -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_boards_list, parent, false)
                EmptyViewHolder(
                    view
                )
            }
            TrelloRepository.Item.TYPE.FINALY_EMPTY.type -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_boards_list, parent, false)
                EmptyViewHolder(
                    view
                )
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_boards_list, parent, false)
                BoardViewHolder(
                    view
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrganizationViewHolder -> holder.bind(GetItem(position).name)
            is EmptyViewHolder -> holder.bind(GetItem(position).name)
            is BoardViewHolder -> {
                val item = GetItem(position)
                with(holder.mView) {
                    tag = item
                    setOnClickListener { v ->
                        val selected_item = v.tag as TrelloRepository.Item
                        onBoardSelectionListener.onBoardSelectionCall(selected_item.id)
                    }
                }
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun GetItem(position: Int): TrelloRepository.Item {
        return items[position]
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // Проверки.
        if (items[fromPosition] !is TrelloRepository.BoardItem ||
            items[toPosition] !is TrelloRepository.BoardItem) {
            return false
        }

        if (repostitoryViewModel.boardMove(items[fromPosition] as TrelloRepository.BoardItem,
                items[toPosition] as TrelloRepository.BoardItem, fromPosition, toPosition)) {
            Collections.swap(items, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
            return true
        }
        return false
    }

    override fun onItemDismiss(position: Int) {
        Log.d("Remove", "Remove from $position from $items")
        var temp = items[position]

        // Не удаляем самый последний элемент
        if (temp.type == TrelloRepository.Item.TYPE.FINALY_EMPTY) {
            // TODO: Как не удалять последнюю строку?
            return
        }

        items.removeAt(position)
        repostitoryViewModel.itemDismiss(items, temp, this)

        notifyItemRemoved(position)
    }

    class BoardViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mNameView: TextView = mView.text

        override fun toString(): String {
            return super.toString() + " '" + mNameView.text + "'"
        }

        fun bind(item : TrelloRepository.Item) {
            mNameView.text = item.name
        }
    }

    class OrganizationViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mOrgNameView: TextView = mView.item_header

        fun bind(string: String) {
            mOrgNameView.text = string
        }

        override fun toString(): String {
            return super.toString() + " '" + mOrgNameView.text + "'"
        }
    }

    class EmptyViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mEmptyView: TextView = mView.content
        fun bind(string: String) {
            mEmptyView.text = string
        }

        override fun toString(): String {
            return super.toString() + " '" + mEmptyView.text + "'"
        }
    }
}

