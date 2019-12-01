package com.example.trello.Fragments.Card

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.example.trello.Data.Card

import com.example.trello.R
import kotlinx.android.synthetic.main.activity_home.*


/**

 */
class CardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var card: Card

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
//            it.toolbar.setOnMenuItemClickListener()
        }

        return view
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
        fun newInstance(newCard: Card) =
            CardFragment().apply {
                arguments = Bundle().apply {
                    card = newCard
                }
            }
    }
}
