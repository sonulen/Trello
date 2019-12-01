package com.example.trello.View.BoardViewComponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.trello.Data.Card
import com.example.trello.R
import com.woxthebox.draglistview.DragItemAdapter
import java.util.*

interface onCardClickListener {
    fun processClick(card: Card)
    fun processDelete(card: Card)
}

internal class ItemAdapter(
    list: ArrayList<Card>,
    private val mLayoutId: Int,
    private val mGrabHandleId: Int,
    private val mDragOnLongPress: Boolean,
    private val listener: onCardClickListener
) :
    DragItemAdapter<Card, ItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)
        val text = mItemList[position].name
        holder.mText.text = text
        holder.itemView.tag = mItemList[position]
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].seq.toLong()
    }

    internal inner class ViewHolder(itemView: View) :
        DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var mText: TextView
        override fun onItemClicked(view: View) {
            var card = itemView.tag as Card
            listener.processClick(card)
        }

        override fun onItemLongClicked(view: View): Boolean {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle("Delete this card?")

            // Set up the buttons
            builder.setPositiveButton(
                "OK"
            ) { _, _ ->
                listener.processDelete(itemView.tag as Card)
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.cancel() }

            builder.show()
            return true
        }

        init {
            mText = itemView.findViewById(R.id.text)
        }
    }

    init {
        itemList = list
    }
}