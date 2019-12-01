package com.example.trello.Fragments.Card

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.Data.Card

import com.example.trello.R
import com.example.trello.View.ActionRecyclerViewComponents.ActionAdapter
import kotlinx.android.synthetic.main.fragment_card.view.*

interface CardRemoveInterface {
    fun remove(card: Card)
}

/**

 */
class CardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var card: Card
    private lateinit var listener: CardRemoveInterface

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_card, container, false)


        // Изменим титул и хендлер (сюда из backstack попадаем тоже)
        activity?.let{
            it.title = card.name
        }

        view.actionRecycler.let {
            with(view.actionRecycler) {
                viewManager = LinearLayoutManager(activity)
                viewAdapter = ActionAdapter(activity!!, card.data?.actions ?: listOf())

                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        fill(view)

        view.removeCardBtn.setOnClickListener {
            listener.remove(card)
            activity?.supportFragmentManager?.popBackStack()
        }

        return view
    }

    private fun fill(view: View) {

        card.data?.let {
            view.cardName.text = card.name
            view.fromBoard.text = it.board?.name ?: "Empty"
            view.desc.text = it.desc ?: "Empty"
            view.url.text = it.shortUrl ?: "Empty"
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Card.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            newCard: Card,
            cardRemoveInterface: CardRemoveInterface
        ) =
            CardFragment().apply {
                arguments = Bundle().apply {
                    card = newCard
                    listener = cardRemoveInterface
                }
            }
    }
}
