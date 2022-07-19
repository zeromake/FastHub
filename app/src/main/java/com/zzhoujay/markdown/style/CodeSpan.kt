package com.zzhoujay.markdown.style

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.style.ReplacementSpan

/**
 * Created by zhou on 16-7-2.
 * 代码Span
 */
class CodeSpan : ReplacementSpan {
    private var radius = 10f
    private val drawable: Drawable
    private var padding = 0f
    private var width = 0
    private var textColor = 0

    constructor(color: Int) {
        val d = GradientDrawable()
        d.setColor(color)
        d.cornerRadius = radius
        drawable = d
    }

    constructor(color: Int, textColor: Int, radius: Float) {
        this.radius = radius
        this.textColor = textColor
        val d = GradientDrawable()
        d.setColor(color)
        d.cornerRadius = radius
        drawable = d
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        padding = paint.measureText("t")
        width = (paint.measureText(text, start, end) + padding * 2).toInt()
        return width
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        drawable.setBounds(x.toInt(), top, x.toInt() + width, bottom)
        drawable.draw(canvas)
        if (textColor != 0) {
            paint.color = textColor
        }
        canvas.drawText(text, start, end, x + padding, y.toFloat(), paint)
    }
}