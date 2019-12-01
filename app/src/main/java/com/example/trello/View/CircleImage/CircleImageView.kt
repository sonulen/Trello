package com.example.trello.View.CircleImage

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider

import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.example.trello.R

/**
 * View для отображения изображения в кругу
 *
 * Аттрибуты CircleImageView:
 * - strokeColor            Цвет обводки
 * - strokeWidth            Ширина обводки
 * - highlightEnable        Включено ли выделение при нажатии (по умолч вкл)
 * - highlightColor         Цвет заливки при нажатии
 *
*/
open class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var mBitmapShader: Shader? = null
    private val mShaderMatrix: Matrix

    private val mBitmapDrawBounds: RectF
    private val mStrokeBounds: RectF

    private var mBitmap: Bitmap? = null

    private val mBitmapPaint: Paint
    private val mStrokePaint: Paint
    private val mPressedPaint: Paint

    private val mInitialized: Boolean
    private var mPressed: Boolean = false
    private var mHighlightEnable: Boolean = false

    // Публичные поля для взаимодействия с CircleImage
    // В сетерах вызывается перерисовка View

    var isHighlightEnable: Boolean
        get() = mHighlightEnable
        set(enable) {
            mHighlightEnable = enable
            invalidate()
        }

    var highlightColor: Int
        get() = mPressedPaint.color
        set(@ColorInt color) {
            mPressedPaint.color = color
            invalidate()
        }

    var strokeColor: Int
        get() = mStrokePaint.color
        set(@ColorInt color) {
            mStrokePaint.color = color
            invalidate()
        }

    var strokeWidth: Float
        get() = mStrokePaint.strokeWidth
        set(@Dimension width) {
            mStrokePaint.strokeWidth = width
            invalidate()
        }

    init {

        var strokeColor = Color.TRANSPARENT
        var strokeWidth = 0f
        var highlightEnable = true
        var highlightColor =
            DEF_PRESS_HIGHLIGHT_COLOR

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs,
                R.styleable.CircleImageView, 0, 0)

            strokeColor = a.getColor(R.styleable.CircleImageView_strokeColor, Color.TRANSPARENT)
            strokeWidth =
                a.getDimensionPixelSize(R.styleable.CircleImageView_strokeWidth, 0).toFloat()
            highlightEnable = a.getBoolean(R.styleable.CircleImageView_highlightEnable, true)
            highlightColor =
                a.getColor(
                    R.styleable.CircleImageView_highlightColor,
                    DEF_PRESS_HIGHLIGHT_COLOR
                )

            a.recycle()
        }

        // ANTI_ALIAS_FLAG - сглаживает диагонали, но отжирает по производительности
        mShaderMatrix = Matrix()
        // Создадим кисти для отрисовки картинки и stroke
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)

        mStrokeBounds = RectF()
        mBitmapDrawBounds = RectF()

        mStrokePaint.color = strokeColor
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.strokeWidth = strokeWidth

        mPressedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPressedPaint.color = highlightColor
        mPressedPaint.style = Paint.Style.FILL

        mHighlightEnable = highlightEnable
        mInitialized = true

        setupBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val halfStrokeWidth = mStrokePaint.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mStrokeBounds.set(mBitmapDrawBounds)
        mStrokeBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = CircleImageViewOutlineProvider(mStrokeBounds)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var processed = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isInCircle(event.x, event.y)) {
                    return false
                }
                processed = true
                mPressed = true
                invalidate()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                processed = true
                mPressed = false
                invalidate()
                if (!isInCircle(event.x, event.y)) {
                    return false
                }
            }
        }
        return super.onTouchEvent(event) || processed
    }

    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
        drawStroke(canvas)
        drawHighlight(canvas)
    }

    protected fun drawHighlight(canvas: Canvas) {
        if (mHighlightEnable && mPressed) {
            canvas.drawOval(mBitmapDrawBounds, mPressedPaint)
        }
    }

    protected fun drawStroke(canvas: Canvas) {
        if (mStrokePaint.strokeWidth > 0f) {
            canvas.drawOval(mStrokeBounds, mStrokePaint)
        }
    }

    protected fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }

    protected fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = Math.min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        if (!mInitialized) {
            return
        }

        // Загрузим bitmap
        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }

        // Создадим bitmapshader
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        // Вырежем круг
        updateBitmapSize()
    }

    // Функция обновления размера Bitmap
    private fun updateBitmapSize() {
        if (mBitmap == null) return

        var dx: Float
        var dy: Float
        var scale: Float

        // Масштабируем относительно размера границ и исходного изображения
        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapDrawBounds.width() / mBitmap!!.width.toFloat()
            dx = mBitmapDrawBounds.left
            dy =
                mBitmapDrawBounds.top - mBitmap!!.height * scale / 2f + mBitmapDrawBounds.width() / 2f
        } else {
            scale = mBitmapDrawBounds.height() / mBitmap!!.height.toFloat()
            dx =
                mBitmapDrawBounds.left - mBitmap!!.width * scale / 2f + mBitmapDrawBounds.width() / 2f
            dy = mBitmapDrawBounds.top
        }
        mShaderMatrix.setScale(scale, scale)
        // Сместим изображение не центр
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        // дефолт
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun isInCircle(x: Float, y: Float): Boolean {
        // Функция рассчитывает был ли клик в круге
        val distance = Math.sqrt(
            Math.pow(
                (mBitmapDrawBounds.centerX() - x).toDouble(),
                2.0
            ) + Math.pow((mBitmapDrawBounds.centerY() - y).toDouble(), 2.0)
        )
        return distance <= mBitmapDrawBounds.width() / 2
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    inner class CircleImageViewOutlineProvider internal constructor(rect: RectF) :
        ViewOutlineProvider() {

        private val mRect: Rect

        init {
            mRect = Rect(
                rect.left.toInt(),
                rect.top.toInt(),
                rect.right.toInt(),
                rect.bottom.toInt()
            )
        }

        override fun getOutline(view: View, outline: Outline) {
            outline.setOval(mRect)
        }

    }

    companion object {
        private val DEF_PRESS_HIGHLIGHT_COLOR = 0x32000000
    }
}