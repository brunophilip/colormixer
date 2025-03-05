package com.bpi.colormixer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class ColorWheelView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) * 0.9f  // Cercle plus grand

        val primaryColors = intArrayOf(Color.RED, Color.YELLOW, Color.BLUE)
        val secondaryColors = intArrayOf(
            Color.rgb(255, 165, 0), // Orange
            Color.GREEN,
            Color.rgb(150, 0, 255)  // Violet corrigé
        )

        // Organisation des cercles :
        drawColorRing(canvas, centerX, centerY, radius * 0.9f, primaryColors, secondaryColors, 0.5f)  // Extérieur (blanc)
        drawColorRing(canvas, centerX, centerY, radius * 0.7f, primaryColors, secondaryColors, 0f)    // Centre (pur)
        drawColorRing(canvas, centerX, centerY, radius * 0.5f, primaryColors, secondaryColors, -0.5f) // Intérieur (noir)
    }

    private fun drawColorRing(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        primaryColors: IntArray,
        secondaryColors: IntArray,
        lightnessOffset: Float
    ) {
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        val colors = IntArray(6)
        for (i in 0 until 3) {
            colors[i * 2] = adjustLightness(primaryColors[i], lightnessOffset)
            colors[i * 2 + 1] = adjustLightness(secondaryColors[i], lightnessOffset)
        }

        val sectionAngle = 360f / colors.size
        for (i in colors.indices) {
            paint.color = colors[i]
            canvas.drawArc(rectF, i * sectionAngle, sectionAngle, true, paint)
        }
    }

    private fun adjustLightness(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)

        if (factor > 0) {
            // Mélange avec du blanc
            return mixColors(color, Color.WHITE, factor * 0.7f)
        } else {
            // Assombrir en diminuant la valeur (V) de HSV
            hsv[2] = (hsv[2] + factor).coerceIn(0f, 1f)
            return Color.HSVToColor(hsv)
        }
    }

    private fun mixColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1 - ratio
        val r = (Color.red(color1) * inverseRatio + Color.red(color2) * ratio).toInt()
        val g = (Color.green(color1) * inverseRatio + Color.green(color2) * ratio).toInt()
        val b = (Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio).toInt()
        return Color.rgb(r, g, b)
    }
}
