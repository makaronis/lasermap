package com.makaroni.lasermap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat

class MapView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)


    var data: List<List<Byte>> = emptyList()

    val firstPaint = Paint().apply {
        isFilterBitmap = false
        strokeWidth = 0f
        isAntiAlias = true
        color = Color.BLUE
    }

    val secondPaint = Paint().apply {
        isFilterBitmap = false
        strokeWidth = 0f
        isAntiAlias = true
        color = Color.GREEN
    }

    val thirdPaint = Paint().apply {
        isFilterBitmap = false
        strokeWidth = 0f
        isAntiAlias = true
        color = Color.RED
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d("TAG","onDraw")
        data.forEachIndexed { firstIndex, list ->
            Log.d("TAG","index =$firstIndex")
            list.forEachIndexed { index, byte ->
                val paint = when (byte) {
                    1.toByte() -> firstPaint
                    2.toByte() -> secondPaint
                    3.toByte() -> thirdPaint
                    else -> return@forEachIndexed
                }
                canvas?.drawPoint(firstIndex.toFloat(), index.toFloat(), paint)
            }
        }
        super.onDraw(canvas)
    }
}