package com.example.trello.Fragments.Board

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.Data.Card
import com.example.trello.Data.List
import com.example.trello.Fragments.Boards.ProvidesFragmentPlaceholder

import com.example.trello.R
import com.example.trello.View.BoardViewComponents.ItemAdapter
import com.example.trello.ViewModels.TrelloRepositoryBoard.TrelloRepositoryBoardViewModel
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.DragItem
import kotlinx.android.synthetic.main.column_drag_layout.view.*
import kotlinx.android.synthetic.main.column_header.view.*
import kotlinx.android.synthetic.main.column_item.view.*
import kotlinx.android.synthetic.main.fragment_board.view.*
import java.util.ArrayList


class BoardFragment : Fragment() {
    private lateinit var boardViewModel: TrelloRepositoryBoardViewModel
    private lateinit var placeholder: ProvidesFragmentPlaceholder
    private var sCreatedItems = 0
    private var mColumns: Int = 0

    private var columnDragFirstTime = true
    private var oldColumnPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_board, container, false)

        // Изменим титул и хендлер (сюда из backstack попадаем тоже)
        activity?.let{
            it.title = boardViewModel.getBoardName()
        }

        v?.let {
            init_board(v)
            resetBoard(v)
        }

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ProvidesFragmentPlaceholder) {
            placeholder = context
        } else {
            throw RuntimeException(context.toString() + " must implement ProvidesFragmentPlaceholder")
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    ////////////////////////// Функции работы с Board Recycler View ///////////////////////////
    private fun init_board(v : View) {
        var mBoardView = v.board_view
        mBoardView.setSnapToColumnsWhenScrolling(true)
        mBoardView.setSnapToColumnWhenDragging(true)
        mBoardView.setSnapDragItemToTouch(true)
        mBoardView.setSnapToColumnInLandscape(false)
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)
        mBoardView.setBoardListener(object : BoardView.BoardListener {

            override fun onItemDragStarted(column: Int, row: Int) {
            }

            override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                if (fromColumn != toColumn || fromRow != toRow) {
                    var card = mBoardView.getAdapter(toColumn).itemList[toRow] as Card
                    boardViewModel.moveCard(card, toColumn, toRow)
                }
            }

            override fun onItemChangedPosition(
                oldColumn: Int,
                oldRow: Int,
                newColumn: Int,
                newRow: Int
            ) {
            }

            override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {
                val itemCount1 = mBoardView.getHeaderView(oldColumn).item_count
                itemCount1.text = mBoardView.getAdapter(oldColumn).itemCount.toString()
                val itemCount2 = mBoardView.getHeaderView(newColumn).item_count
                itemCount2.text = mBoardView.getAdapter(newColumn).itemCount.toString()
            }

            override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {
            }

            override fun onColumnDragStarted(position: Int) {
            }

            override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {
                if (columnDragFirstTime) {
                    oldColumnPosition = oldPosition
                    columnDragFirstTime = false
                }
            }

            override fun onColumnDragEnded(position: Int) {
                columnDragFirstTime = true
                boardViewModel.changeColumPos(oldColumnPosition, position)
            }
        })
        mBoardView.setBoardCallback(object : BoardView.BoardCallback {
            override fun canDragItemAtPosition(column: Int, dragPosition: Int): Boolean {
                // Add logic here to prevent an item to be dragged
                return true
            }

            override fun canDropItemAtPosition(
                oldColumn: Int,
                oldRow: Int,
                newColumn: Int,
                newRow: Int
            ): Boolean {
                // Add logic here to prevent an item to be dropped
                return true
            }
        })
    }

    private fun resetBoard(v : View) {
        var mBoardView = v.board_view
        mBoardView.clearBoard()
        mBoardView.setCustomDragItem(
            MyDragItem(
                activity!!,
                R.layout.column_item
            )
        )
        mBoardView.setCustomColumnDragItem(
            MyColumnDragItem(
                activity!!,
                R.layout.column_drag_layout
            )
        )

        // Загрузим колонки из доски
        var sortedListBySeq = boardViewModel.getBoardColumns().values.sortedBy { it.seq }
        for (list in sortedListBySeq) {
            addColumn(v, list)
        }

    }

    /**
     * Функция добавления новой колонки
     * @param v
     * @param list Список из которого генерим
     */
    private fun addColumn(v : View, list : List) {
        var mBoardView = v.board_view
        val mItemArray = ArrayList<Card>()

        var sortedCardsBySeq = list.cards.values.sortedBy { it.seq }

        for (i in  sortedCardsBySeq) {
            mItemArray.add(i)
        }

        val column = mColumns
        val listAdapter = ItemAdapter(
            mItemArray,
            R.layout.column_item,
            R.id.item_layout,
            true,
            list.id
        )
        val header = View.inflate(activity, R.layout.column_header, null)

        (header.findViewById(R.id.text) as TextView).text = list.name
        (header.findViewById(R.id.item_count) as TextView).text = "Count of cards:" + list.cards.count()

        header.add_item_image.setOnClickListener { view ->
            addButtonOnClickListener(view, mItemArray,mBoardView, column, list, header)
        }

        mBoardView.addColumn(
            listAdapter,
            header,
            header,
            false,
            LinearLayoutManager(
                context
            )
        )
        mColumns++
    }

    private fun addButtonOnClickListener(
        view: View,
        mItemArray: ArrayList<Card>,
        mBoardView: BoardView,
        column: Int,
        list: List,
        header: View
    ) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Create new card")
        // Создадим layout в который уместим EditText
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
                addCard(mItemArray, mBoardView, column, list, header, input.text.toString())
            }

        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        // Добавим на layout наш edittext
        linearLayout.addView(input)

        // Отобразим получившийся view
        builder.setView(linearLayout)
        builder.show()
    }

    private fun addCard(
        mItemArray: ArrayList<Card>,
        mBoardView: BoardView,
        column: Int,
        list: List,
        header: View,
        name: String
    ) {
        val id = sCreatedItems++.toLong()
        val seq = mItemArray.count()
        val item = Card(id.toString(), list.id, list.idBoard, name, seq)
        mBoardView.addItem(column, seq, item, true)
        (header.findViewById(R.id.item_count) as TextView).text = "Count of cards:" + mItemArray.size.toString()
        boardViewModel.addCard(list.id, item.name)
    }

    /**
     * Вспомогательный класс перемещения элемента
     */
    private class MyDragItem internal constructor(context: Context, layoutId: Int) :
        DragItem(context, layoutId) {

        override fun onBindDragView(clickedView: View, dragView: View) {
            val text = (clickedView.findViewById(R.id.text) as TextView).text
            (dragView.findViewById(R.id.text) as TextView).text = text
            val dragCard = dragView.card
            val clickedCard = clickedView.card

            dragCard.maxCardElevation = 40f
            dragCard.cardElevation = clickedCard.cardElevation
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
            dragCard.foreground = clickedView.resources.getDrawable(R.drawable.card_view_drag_foreground, null)
        }

        override fun onMeasureDragView(clickedView: View, dragView: View) {
            val dragCard = dragView.card
            val clickedCard = clickedView.card
            val widthDiff =
                dragCard.paddingLeft - clickedCard.paddingLeft + dragCard.paddingRight - clickedCard.paddingRight
            val heightDiff =
                dragCard.paddingTop - clickedCard.paddingTop + dragCard.paddingBottom - clickedCard.paddingBottom
            val width = clickedView.measuredWidth + widthDiff
            val height = clickedView.measuredHeight + heightDiff
            dragView.layoutParams = FrameLayout.LayoutParams(width, height)

            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            dragView.measure(widthSpec, heightSpec)
        }

        override fun onStartDragAnimation(dragView: View) {
            val dragCard = dragView.card
            val anim =
                ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = DragItem.ANIMATION_DURATION.toLong()
            anim.start()
        }

        override fun onEndDragAnimation(dragView: View) {
            val dragCard = dragView.card
            val anim =
                ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = DragItem.ANIMATION_DURATION.toLong()
            anim.start()
        }
    }

    /**
     * Вспомогательный класс перемещения колонки
     */
    private class MyColumnDragItem internal constructor(context: Context, layoutId: Int) :
        DragItem(context, layoutId) {

        init {
            setSnapToTouch(false)
        }

        override fun onBindDragView(clickedView: View, dragView: View) {
            val clickedLayout = clickedView as LinearLayout
            val clickedHeader = clickedLayout.getChildAt(0)
            val clickedRecyclerView = clickedLayout.getChildAt(1) as RecyclerView

            val dragHeader = dragView.drag_header
            val dragScrollView = dragView.drag_scroll_view
            val dragLayout = dragView.drag_list
            dragLayout.removeAllViews()

            (dragHeader.findViewById(R.id.text) as TextView).text =
                (clickedHeader.findViewById(R.id.text) as TextView).text
            (dragHeader.findViewById(R.id.item_count) as TextView).text =
                (clickedHeader.findViewById(R.id.item_count) as TextView).text
            for (i in 0 until clickedRecyclerView.childCount) {
                val view = View.inflate(dragView.context,
                    R.layout.column_item, null)
                (view.findViewById(R.id.text) as TextView).text =
                    (clickedRecyclerView.getChildAt(i).findViewById(R.id.text) as TextView).text
                dragLayout.addView(view)

                if (i == 0) {
                    dragScrollView.scrollY = -clickedRecyclerView.getChildAt(i).top
                }
            }

            dragView.pivotY = 0f
            dragView.pivotX = (clickedView.getMeasuredWidth() / 2).toFloat()
        }

        override fun onStartDragAnimation(dragView: View?) {
            super.onStartDragAnimation(dragView)
            dragView!!.animate().scaleX(0.9f).scaleY(0.9f).start()
        }

        override fun onEndDragAnimation(dragView: View?) {
            super.onEndDragAnimation(dragView)
            dragView!!.animate().scaleX(1f).scaleY(1f).start()
        }
    }

    // Фабрика
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment Board.
         */
        @JvmStatic
        fun newInstance(newBoardViewModel: TrelloRepositoryBoardViewModel) =
            BoardFragment().apply {
                boardViewModel = newBoardViewModel
            }
    }
}
