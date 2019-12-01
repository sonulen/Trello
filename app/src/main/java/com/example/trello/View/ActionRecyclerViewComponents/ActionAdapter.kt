package com.example.trello.View.ActionRecyclerViewComponents

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.Network.TrelloService.ActionData
import com.example.trello.R


internal class ActionAdapter(context: Context, private val actions: List<ActionData>) :
    RecyclerView.Adapter<ActionAdapter.ViewHolder>() {
    private val inflater: LayoutInflater

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = inflater.inflate(R.layout.action_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val action = actions[position]
        holder.type.text = action.type
        holder.data.text = action.date
        holder.author.text = action.memberCreator!!.fullName
    }

    override fun getItemCount(): Int {
        return actions.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val type: TextView = view.findViewById(R.id.type)
        val data: TextView = view.findViewById(R.id.data)
        val author: TextView = view.findViewById(R.id.author)
    }

    init {
        inflater = LayoutInflater.from(context)
    }
}