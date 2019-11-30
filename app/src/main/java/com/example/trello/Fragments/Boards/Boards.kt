package com.example.trello.Fragments.Boards

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trello.Network.TrelloClient.TrelloClient

import com.example.trello.R
import io.reactivex.schedulers.Schedulers


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Boards.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Boards.newInstance] factory method to
 * create an instance of this fragment.
 */
class Boards : Fragment() {
    lateinit var client: TrelloClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_boards, container, false)
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
         */
        @JvmStatic
        fun newInstance(newClient: TrelloClient) =
            Boards().apply {
                client = newClient
            }
    }
}
