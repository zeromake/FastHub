package com.fastaccess.ui.widgets

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.RectF
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.CharacterStyle
import android.text.style.ReplacementSpan
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import com.fastaccess.helper.ViewHelper
import java.util.*

/**
 * adopted class from Android source code & modified to fit FastHub need.
 */
class LabelSpan private constructor(private val color: Int, dims: SpanDimensions) :
    ReplacementSpan() {
    interface SpanDimensions {
        val padding: Int
        val paddingExtraWidth: Int
        val paddingAfter: Int
        val maxWidth: Int
        val roundedCornerRadius: Float
        val marginTop: Int
        val isRtl: Boolean
    }

    private val txtPaint = TextPaint()
    private val fontMetrics = FontMetricsInt()
    private val dims: SpanDimensions

    constructor(color: Int) : this(color, object : SpanDimensions {
        override val padding: Int
            get() = 6
        override val paddingExtraWidth: Int
            get() = 0
        override val paddingAfter: Int
            get() = 0

        //random number
        override val maxWidth: Int
            get() = 1000 //random number
        override val roundedCornerRadius: Float
            get() = 5F
        override val marginTop: Int
            get() = 8
        override val isRtl: Boolean
            get() = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
    })

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        setupFontMetrics(text, start, end, fm, paint)
        if (fm != null) {
            val padding = dims.padding
            val margin = dims.marginTop
            fm.ascent = fm.top.coerceAtMost(fm.ascent - padding) - margin
            fm.descent = fm.bottom.coerceAtLeast(padding)
            fm.top = fm.ascent
            fm.bottom = fm.descent
        }
        return measureWidth(txtPaint, text, start, end, dims.isRtl)
    }

    private fun measureWidth(
        paint: Paint, text: CharSequence, start: Int, end: Int,
        includePaddingAfter: Boolean
    ): Int {
        val paddingW = dims.padding + dims.paddingExtraWidth
        val maxWidth = dims.maxWidth
        var w = paint.measureText(text, start, end).toInt() + 2 * paddingW
        if (includePaddingAfter) {
            w += dims.paddingAfter
        }
        if (w > maxWidth) {
            w = maxWidth
        }
        return w
    }

    private fun setupFontMetrics(
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?,
        p: Paint
    ) {
        txtPaint.set(p)
        val otherSpans = (text as Spanned).getSpans(start, end, CharacterStyle::class.java)
        for (otherSpan in otherSpans) {
            otherSpan.updateDrawState(txtPaint)
        }
        txtPaint.textSize = p.textSize
        if (fm != null) {
            txtPaint.getFontMetricsInt(fm)
        }
    }

    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        var sequence = text
        var start1 = start
        var end1 = end
        val top1: Int
        var y1 = y
        val padding = dims.padding
        val paddingW = padding + dims.paddingExtraWidth
        val maxWidth = dims.maxWidth
        setupFontMetrics(sequence, start1, end1, fontMetrics, paint)
        val bgWidth = measureWidth(txtPaint, sequence, start1, end1, false)
        fontMetrics.top = fontMetrics.top.coerceAtMost(fontMetrics.ascent - padding)
        fontMetrics.bottom = fontMetrics.bottom.coerceAtLeast(padding)
        top1 = y1 + fontMetrics.top - fontMetrics.bottom
        val bottom1: Int = y1
        y1 = bottom1 - fontMetrics.bottom
        val isRtl = dims.isRtl
        val paddingAfter = dims.paddingAfter
        if (txtPaint.bgColor != 0) {
            val prevColor = txtPaint.color
            val prevStyle = txtPaint.style
            txtPaint.color = txtPaint.bgColor
            txtPaint.style = Paint.Style.FILL
            val left: Float = if (isRtl) {
                x + paddingAfter
            } else {
                x
            }
            val right = left + bgWidth
            val rect = RectF(left, top1.toFloat(), right, bottom1.toFloat())
            val radius = dims.roundedCornerRadius
            canvas.drawRoundRect(rect, radius, radius, txtPaint)
            txtPaint.color = prevColor
            txtPaint.style = prevStyle
        }
        if (bgWidth == maxWidth) {
            sequence = TextUtils.ellipsize(
                sequence.subSequence(start1, end1).toString(),
                txtPaint,
                (bgWidth - 2 * paddingW).toFloat(),
                TextUtils.TruncateAt.MIDDLE
            )
            start1 = 0
            end1 = sequence.length
        }
        var textX = x + paddingW
        if (isRtl) {
            textX += paddingAfter.toFloat()
        }
        if (color != Color.TRANSPARENT) txtPaint.color = ViewHelper.generateTextColor(
            color
        )
        canvas.drawText(sequence, start1, end1, textX, y1.toFloat(), txtPaint)
    }

    init {
        txtPaint.bgColor = color
        this.dims = dims
    }
}