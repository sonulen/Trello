package com.example.trello.View.BoardViewComponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.util.Pair
import com.example.trello.R
import com.woxthebox.draglistview.DragItemAdapter
import java.util.*

internal class ItemAdapter(
    list: ArrayList<Pair<Long, String>>,
    private val mLayoutId: Int,
    private val mGrabHandleId: Int,
    private val mDragOnLongPress: Boolean
) :
    DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder>() {
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
        val text = mItemList[position]!!.second
        holder.mText.text = text
        holder.itemView.tag = mItemList[position]
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position]!!.first!!
    }

    internal inner class ViewHolder(itemView: View) :
        DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var mText: TextView
        override fun onItemClicked(view: View) {
            Toast.makeText(view.context, "Item clicked", Toast.LENGTH_SHORT).show()
        }

        override fun onItemLongClicked(view: View): Boolean {
            Toast.makeText(view.context, "Item long clicked", Toast.LENGTH_SHORT).show()
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