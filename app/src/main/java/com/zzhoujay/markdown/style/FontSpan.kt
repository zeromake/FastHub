package com.zzhoujay.markdown.style

import android.annotation.SuppressLint
import android.text.ParcelableSpan
import android.text.TextPaint
import android.text.style.StyleSpan

/**
 * Created by zhou on 2016/11/10.
 * FontSpan
 */
@SuppressLint("ParcelCreator")
class FontSpan : StyleSpan, ParcelableSpan {
    private val size: Float
    private val color: Int

    constructor(size: Float, style: Int) : super(style) {
        this.size = size
        color = -1
    }

    constructor(size: Float, style: Int, color: Int) : super(style) {
        this.size = size
        this.color = color
    }

    override fun updateMeasureState(p: TextPaint) {
        super.updateMeasureState(p)
        p.textSize = p.textSize * size
    }

    override fun updateDrawState(tp: TextPaint) {
        super.updateDrawState(tp)
        updateMeasureState(tp)
        if (color != -1) tp.color = color
    }
}