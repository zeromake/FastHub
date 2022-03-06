package com.fastaccess.ui.widgets

import android.text.SpannableStringBuilder
import com.fastaccess.helper.InputHelper
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.text.style.BackgroundColorSpan
import androidx.annotation.ColorInt
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.text.style.ClickableSpan
import android.text.TextPaint
import android.view.View

/**
 * Created by Kosh on 15 Nov 2016, 9:26 PM
 */
class SpannableBuilder private constructor() : SpannableStringBuilder() {
    fun append(text: CharSequence, span: Any?): SpannableBuilder {
        if (!InputHelper.isEmpty(text)) {
            append(text)
            if (span != null) {
                val length = length
                setSpan(span, length - text.length, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return this
    }

    override fun append(text: Char): SpannableBuilder {
        if (text.code != 0) super.append(text)
        return this
    }

    override fun append(text: CharSequence): SpannableBuilder {
        super.append(text)
        return this
    }

    fun append(span: Any?): SpannableBuilder {
        setSpan(span, length - 1, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    fun append(drawable: Drawable?): SpannableBuilder {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            append(' ', ImageSpan(drawable))
        }
        return this
    }

    fun append(text: Char, span: Any?): SpannableBuilder {
        append(text)
        if (!InputHelper.isEmpty(span)) {
            val length = length
            setSpan(span, length - 1, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return this
    }

    fun bold(text: CharSequence, span: Any?): SpannableBuilder {
        var sequence = text
        if (!InputHelper.isEmpty(sequence)) {
            sequence = builder().bold(sequence)
            append(sequence, span)
        }
        return this
    }

    fun bold(text: CharSequence): SpannableBuilder {
        return if (!InputHelper.isEmpty(text)) append(text, StyleSpan(Typeface.BOLD)) else this
    }

    fun background(text: CharSequence, color: Int): SpannableBuilder {
        return if (!InputHelper.isEmpty(text)) append(text, BackgroundColorSpan(color)) else this
    }

    fun foreground(text: CharSequence, @ColorInt color: Int): SpannableBuilder {
        return if (!InputHelper.isEmpty(text)) append(text, ForegroundColorSpan(color)) else this
    }

    fun foreground(text: Char, @ColorInt color: Int): SpannableBuilder {
        return append(text, ForegroundColorSpan(color))
    }

    fun url(text: CharSequence, listener: View.OnClickListener): SpannableBuilder {
        return if (!InputHelper.isEmpty(text)) append(text, object : URLSpan(text.toString()) {
            override fun onClick(widget: View) {
                listener.onClick(widget)
            }
        }) else this
    }

    fun url(text: CharSequence): SpannableBuilder {
        return if (!InputHelper.isEmpty(text)) append(text, URLSpan(text.toString())) else this
    }

    fun clickable(text: CharSequence, listener: View.OnClickListener): SpannableBuilder {
        return if (!InputHelper.isEmpty(text)) append(text, object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = false
            }

            override fun onClick(widget: View) {
                listener.onClick(widget)
            }
        }) else this
    }

    companion object {
        @JvmStatic
        fun builder(): SpannableBuilder {
            return SpannableBuilder()
        }
    }
}