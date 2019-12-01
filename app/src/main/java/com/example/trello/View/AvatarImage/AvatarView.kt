package com.example.trello_parody.AvatarView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import com.example.trello.R
import com.example.trello.View.CircleImage.CircleImageView

/**
 * View для отображения аватарки либо первой буквы ника.
 *
 * Аттрибуты от CircleImageView:
 * - strokeColor            Цвет обводки
 * - strokeWidth            Ширина обводки
 * - highlightEnable        Включено ли выделение при нажатии (по умолч вкл)
 * - highlightColor         Цвет заливки при нажатии
 *
 * Аттрибуты от AvatarView:
 * - avatarBackgroundColor  Цвет фона (виден когда у нас текст)
 * - textSize               Размер текста
 * - textColor              Цвет текста
 * - text                   Сам текст (возьмётся только 1ая буква)
 * - view_state             Режим. INITIAL - отображаем текст. IMAGE - картинку
 *
 */
class AvatarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    CircleImageView(context, attrs) {

    private val mTextPaint: Paint
    private val mTextBounds: Rect

    private val mBackgroundPaint: Paint
    private val mBackgroundBounds: RectF

    // Отображаемый символ
    var initial: String
        private set

    // Текст заданный через аттр
    private var mText: String

    // Режим отображения текст/картинка
    private var mShowState: Int = 0

    // Публичные поля для взаимодействия с AvatarView
    // В сетерах вызывается перерисовка View
    var text: String?
        get() = mText
        set(text) {
            mText = text ?: ""
            initial = extractInitial(text)
            updateTextBounds()
            invalidate()
        }

    var state: Int
        get() = mShowState
        set(state) {
            if (state != SHOW_INITIAL && state != SHOW_IMAGE) {
                val msg =
                    "Illegal avatar state value: $state, use either SHOW_INITIAL or SHOW_IMAGE constant"
                throw IllegalArgumentException(msg)
            }
            mShowState = state
            invalidate()
        }

    var textSize: Float
        get() = mTextPaint.textSize
        set(size) {
            mTextPaint.textSize = size
            updateTextBounds()
            invalidate()
        }

    var textColor: Int
        get() = mTextPaint.color
        set(color) {
            mTextPaint.color = color
            invalidate()
        }

    var avatarBackgroundColor: Int
        get() = mBackgroundPaint.color
        set(color) {
            mBackgroundPaint.color = color
            invalidate()
        }


    init {
        var text: String? = DEF_INITIAL
        var textColor = Color.WHITE
        var textSize = DEF_TEXT_SIZE
        var backgroundColor =
            DEF_BACKGROUND_COLOR
        var showState = DEF_STATE

        if (attrs != null) {
            // Достаем параметры
            val a = context.obtainStyledAttributes(attrs,
                R.styleable.AvatarView, 0, 0)

            text = a.getString(R.styleable.AvatarView_text)
            textColor = a.getColor(R.styleable.AvatarView_textColor, textColor)
            textSize = a.getDimensionPixelSize(R.styleable.AvatarView_textSize, textSize)
            backgroundColor =
                a.getColor(R.styleable.AvatarView_avatarBackgroundColor, backgroundColor)
            // По умолчанию у нас текст
            showState = a.getInt(R.styleable.AvatarView_view_state, showState)
            // Освобождаем TypedArray
            a.recycle()
        }

        // В зависимости от того было ли заданно состояние мы либо отобразим текст либо изображение
        mShowState = showState
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.color = textColor
        mTextPaint.textSize = textSize.toFloat()

        mTextBounds = Rect()
        mText = text ?: ""
        initial = extractInitial(text)
        // Обновим границы для текста
        updateTextBounds()

        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint.color = backgroundColor
        mBackgroundPaint.style = Paint.Style.FILL

        mBackgroundBounds = RectF()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateCircleDrawBounds(mBackgroundBounds)
    }

    override fun onDraw(canvas: Canvas) {
        if (mShowState == SHOW_INITIAL) {
            // Если сейчас состояние = Выводим текст
            val textBottom = mBackgroundBounds.centerY() - mTextBounds.exactCenterY()
            canvas.drawOval(mBackgroundBounds, mBackgroundPaint)
            canvas.drawText(initial, mBackgroundBounds.centerX(), textBottom, mTextPaint)
            drawStroke(canvas)
            drawHighlight(canvas)
        } else {
            // Иначе вызываем onDraw CircleImageView
            super.onDraw(canvas)
        }
    }

    private fun extractInitial(letter: String?): String {
        // Выводим всегда либо первый символ либо ?
        return if (letter == null || letter.trim { it <= ' ' }.length <= 0) "?" else letter[0].toString()
    }

    private fun updateTextBounds() {
        mTextPaint.getTextBounds(initial, 0, initial.length, mTextBounds)
    }

    companion object {

        val SHOW_INITIAL = 1 ///< Показать текст
        val SHOW_IMAGE = 2 ///< Показать изображение

        // Значения по умолчанию для текста
        private val DEF_INITIAL = "U"
        private val DEF_TEXT_SIZE = 90
        private val DEF_BACKGROUND_COLOR = 0xE53935
        private val DEF_STATE = SHOW_INITIAL
    }
}