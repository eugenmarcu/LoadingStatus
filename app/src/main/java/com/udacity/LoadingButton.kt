package com.udacity

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()


    private var downloading = false
    var textOfTheButton = DOWNLOAD
    var currentWidth = 0
    var currentDegree = 0

    companion object {
        const val DOWNLOAD = "Download"
        const val PROPERTY_RECT = "rect"
        const val PROPERTY_ARC = "arc"

        private var ARC_RADIUS = 40f
        private var LEFT = 500f
        private var TOP = 20f
        private var RIGHT = 0f
        private var BOTTOM = TOP + ARC_RADIUS * 2
    }

    private var circle = RectF(0f, 0f, 0f, 0f)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                loadingAnimation()
                downloading = true //false
                custom_button.isClickable = false
                textOfTheButton = context.getString(R.string.downloading)
            }
            ButtonState.Loading -> {
                if (valueAnimator.isPaused)
                    valueAnimator.resume()
                downloading = true
                custom_button.isClickable = false
                textOfTheButton = context.getString(R.string.please_wait)
                invalidate()
            }
            ButtonState.Completed -> {
                valueAnimator.end()
                downloading = false
                custom_button.isClickable = true
                textOfTheButton = context.getString(R.string.download)
                invalidate()
            }
        }
    }

    private fun loadingAnimation() {
        valueAnimator.setValues(
            PropertyValuesHolder.ofInt(PROPERTY_RECT, 0, widthSize),
            PropertyValuesHolder.ofInt(PROPERTY_ARC, 0, 360)
        )
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 2000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.addUpdateListener {
            currentWidth = it.getAnimatedValue(PROPERTY_RECT) as Int
            currentDegree = it.getAnimatedValue(PROPERTY_ARC) as Int
            invalidate()
        }
        valueAnimator.start()
    }

    fun setButtonStatus(buttonState: ButtonState) {
        (context as Activity).runOnUiThread {
            this.buttonState = buttonState
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = resources.getColor(R.color.colorPrimary)
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        if (downloading) {
            paint.color = resources.getColor(R.color.colorPrimaryDark)
            canvas?.drawRect(
                0f,
                0f,
                currentWidth.toFloat(),
                heightSize.toFloat(),
                paint
            )

            paint.color = resources.getColor((R.color.colorAccent))
            canvas?.drawArc(
                circle, 0f,
                currentDegree.toFloat(), true, paint
            )
        }

        paint.color = Color.WHITE
        canvas?.drawText(textOfTheButton, widthSize / 2f, heightSize / 2f + 10f, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        LEFT = w - 150f
        RIGHT = LEFT + ARC_RADIUS * 2
        TOP = h / 2 - ARC_RADIUS
        BOTTOM = TOP + ARC_RADIUS * 2
        circle = RectF(
            LEFT,
            TOP,
            RIGHT,
            BOTTOM
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h

        setMeasuredDimension(w, h)
    }

}