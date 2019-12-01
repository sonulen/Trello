package com.example.trello.Fragments.Boards

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trello.Data.BoardInteraction
import com.example.trello.Data.RepostirotyState
import com.example.trello.Fragments.Board.BoardFragment
import com.example.trello.Network.TrelloClient.TrelloClient
import com.example.trello.R
import com.example.trello.TrelloBoards.HomePage.BoardTouchHelperAdapter
import com.example.trello.TrelloBoards.HomePage.BoardTouchHelperCallback
import com.example.trello.TrelloBoards.HomePage.MyBoardsRecyclerViewAdapter
import com.example.trello.ViewModels.TrelloRepository.TrelloRepositoryViewModel
import com.example.trello.ViewModels.TrelloRepository.TrelloRepositoryViewModelFactory
import com.example.trello.ViewModels.TrelloRepositoryBoard.TrelloRepositoryBoardViewModel
import kotlinx.android.synthetic.main.fragment_boards.view.*

/**
 * Интерфейс для получения placeholder для Фрагментов
 */
interface ProvidesFragmentPlaceholder {
    fun getPlaceholderID() : Int
}

interface onBoardSelectionListener {
    fun onBoardSelectionCall(idBoard: String)
}
/**
 *
 */
class Boards : Fragment(), onBoardSelectionListener {
    lateinit var client: TrelloClient
    lateinit var repositoryViewModel: TrelloRepositoryViewModel
    private lateinit var boardsAdapter : MyBoardsRecyclerViewAdapter
    private lateinit var placeholder: ProvidesFragmentPlaceholder
    private var firstLoadFlag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repositoryViewModel = ViewModelProviders
            .of(this, TrelloRepositoryViewModelFactory(client))[TrelloRepositoryViewModel::class.java]

        boardsAdapter = MyBoardsRecyclerViewAdapter(
            repositoryViewModel,
            this as onBoardSelectionListener,
            activity!!
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_boards, container, false)

        // Изменим титул и хендлер (сюда из backstack попадаем тоже)
        activity?.let {
            it.title = "All boards"
        }

        view.swiperefresh.setOnRefreshListener {
            repositoryViewModel.refresh()
        }

        repositoryViewModel.getState().observe(this, Observer {
            when(it) {
                RepostirotyState.NOT_INITED -> {}
                RepostirotyState.LOADING -> {
                    showLoadingImage(view)
                }
                RepostirotyState.UPDATED -> {
                    hideLoadingImage(view)
                    view.swiperefresh.isRefreshing = false
                }
                RepostirotyState.FAILED -> {
                    hideLoadingImage(view)
                    view.swiperefresh.isRefreshing = false
                    Toast.makeText(view.context, "Something went wrong, swipe to refresh", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        })

        view.fab.setOnClickListener {
            onFabClick(it)
        }

        // Set the Recycler view adapter
        view.recycler_view_boards.let {
            with(view.recycler_view_boards) {
                layoutManager = LinearLayoutManager(this.context)
                adapter = boardsAdapter

                val callback =
                    BoardTouchHelperCallback(
                        adapter as BoardTouchHelperAdapter
                    )
                val itemTouchHelper = ItemTouchHelper(callback)
                itemTouchHelper.attachToRecyclerView(view.recycler_view_boards)
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ProvidesFragmentPlaceholder) {
            placeholder = context
        } else {
            throw RuntimeException(context.toString() + " must implement ProvidesFragmentPlaceholder")
        }
    }

    private fun hideLoadingImage(view: View) {
        firstLoadFlag = false
        view.home_loading_image.visibility = View.INVISIBLE
        view.boards_content.visibility = View.VISIBLE

    }

    private fun showLoadingImage(view: View) {
        if (firstLoadFlag) {
            view.home_loading_image.visibility = View.VISIBLE
            view.boards_content.visibility = View.INVISIBLE
        }
    }

    private fun onFabClick(view: View) {
        if (activity == null) return

        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Create new board")

        // Создадим layout в который уместим spinner & EditText
        val linearLayout = LinearLayout(view.context)

        val linearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        linearLayout.layoutParams = linearLayoutParams
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.id = View.generateViewId()
        linearLayout.gravity = Gravity.CENTER
        linearLayout.setPadding(15,10,5,5)

        // Add spinner with category's
        // Сгенерим лист из enum нашего. Как можно было сделать в одну строку?
        val s: List<String> = repositoryViewModel.getOrgList()

        // Адаптер для списка
        val adp = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_spinner_dropdown_item, s
        )

        val sp = Spinner(view.context)
        sp.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        sp.adapter = adp
        sp.id = View.generateViewId()


        // Set up the input
        val input = EditText(view.context)
        input.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = "Enter name here"
        input.id = View.generateViewId()


        // Set up the buttons
        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            if (input.text.toString().isNotEmpty()) {
                repositoryViewModel.addBoard(
                    input.text.toString(),
                    sp.selectedItem.toString()
                )
            }

        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        // Добавим spinner на layout
        linearLayout.addView(sp)

        // Добавим на layout наш edittext
        linearLayout.addView(input)

        // Отобразим получившийся view
        builder.setView(linearLayout)
        builder.show()
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

    override fun onBoardSelectionCall(idBoard: String) {
        var boardViewModel = repositoryViewModel.getBoardViewModel(idBoard)

        fragmentManager?.beginTransaction()
            ?.replace(placeholder.getPlaceholderID(), BoardFragment.newInstance(boardViewModel!!))
            ?.addToBackStack("Selected Board")
            ?.commit()
    }
}
