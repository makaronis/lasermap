package com.makaroni.lasermap

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class MapView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var data: List<List<Byte>> = emptyList()

    val firstPaint = Paint().apply {
        isFilterBitmap = false
        strokeWidth = 1f
        isAntiAlias = false
        color = Color.BLUE
    }

    val secondPaint = Paint().apply {
        isFilterBitmap = false
        strokeWidth = 1f
        isAntiAlias = false
        color = Color.GREEN
    }

    val thirdPaint = Paint().apply {
        isFilterBitmap = false
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        strokeWidth = 1f
        isAntiAlias = false
        color = Color.RED
    }

    private var scaleFactor = 1.5f

    private val minScaleFactor = 1f
    private val maxScaleFactor = 1000.0f

    private val scaleListener = ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            val scale = detector?.scaleFactor ?: return false
            Log.d("TAG", "scale = $scale")
            scaleFactor =
                    Math.max(minScaleFactor, Math.min(scaleFactor * scale, maxScaleFactor))
            scaleX *= scaleFactor
            scaleY *= scaleFactor
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
        }
    })

    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private var posX = 0f
    private var posY = 0f

    private var mActivePointerId = MotionEvent.INVALID_POINTER_ID

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleListener.onTouchEvent(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y

                lastTouchX = x;
                lastTouchY = y;
                mActivePointerId = event.getPointerId(0);
            }
            MotionEvent.ACTION_MOVE -> {
                try {
                    val pointerIndex = event.findPointerIndex(mActivePointerId);
                    val x = event.getX(pointerIndex);
                    val y = event.getY(pointerIndex);

                    // Only move if the ScaleGestureDetector isn't processing a gesture.
                    if (!scaleListener.isInProgress) {
                        val dx = x - lastTouchX
                        val dy = y - lastTouchY

                        posX += dx;
                        posY += dy;

                        invalidate();
                    }

                    lastTouchX = x;
                    lastTouchY = y;
                } catch (e: Exception) {
                    Log.e("TAG", "error", e)
                }
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = (event.action and MotionEvent.ACTION_POINTER_INDEX_MASK)
//                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                val pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event.getX(newPointerIndex);
                    lastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        Log.d("TAG", "onMEasure")
    }

    fun createBitmap() {
        if (data.isEmpty()) return
        val width = data.first().size
        val height = data.size
        val bitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        data.forEachIndexed { firstIndex, list ->
//            Log.d("TAG", "index =$firstIndex")
            list.forEachIndexed { index, byte ->
                val paint = when (byte) {
                    1.toByte() -> firstPaint
                    2.toByte() -> secondPaint
                    3.toByte() -> return@forEachIndexed
                    else -> return@forEachIndexed
                }
                canvas.drawPoint(index.toFloat() + 0.5f, firstIndex.toFloat() + 0.5f, paint)
            }
        }
        setImageBitmap(bitmap)
    }

//    override fun onDraw(canvas: Canvas?) {
////        Log.d("TAG", "onDraw")
//        if (data.isNotEmpty()) {
//            canvas?.translate(posX, posY)
//            canvas?.drawRect(0f, 0f, data.size.toFloat(), data[0].size.toFloat(), thirdPaint)
//        }
//        data.forEachIndexed { firstIndex, list ->
////            Log.d("TAG", "index =$firstIndex")
//            list.forEachIndexed { index, byte ->
//                val paint = when (byte) {
//                    1.toByte() -> firstPaint
//                    2.toByte() -> secondPaint
//                    3.toByte() -> return@forEachIndexed
//                    else -> return@forEachIndexed
//                }
//                canvas?.drawPoint(index.toFloat(), firstIndex.toFloat(), paint)
//            }
//        }
//
//        super.onDraw(canvas)
//    }
}