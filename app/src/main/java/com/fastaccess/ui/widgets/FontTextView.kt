package com.fastaccess.ui.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import com.fastaccess.R
import com.fastaccess.helper.TypeFaceHelper
import com.fastaccess.helper.ViewHelper

/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
class FontTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    @State
    var tintColor = -1

    @State
    var mSelected = false
    override fun onSaveInstanceState(): Parcelable {
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state))
        tintDrawables(tintColor)
        isSelected = mSelected
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        this.mSelected = selected
    }

    override fun setText(text: CharSequence, type: BufferType) {
        try {
            super.setText(text, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setTextCursorDrawable(textCursorDrawable: Drawable?) {
        try {
            super.setTextCursorDrawable(textCursorDrawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }}

    override fun setTextCursorDrawable(textCursorDrawable: Int) {
        try {
            super.setTextCursorDrawable(textCursorDrawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val tp = context.obtainStyledAttributes(attributeSet, R.styleable.FontTextView)
            try {
                val color = tp.getColor(R.styleable.FontTextView_drawableColor, -1)
                tintDrawables(color)
            } finally {
                tp.recycle()
            }
        }
        if (isInEditMode) return
        freezesText = true
        TypeFaceHelper.applyTypeface(this)
    }

    fun tintDrawables(@ColorInt color: Int) {
        if (color != -1) {
            tintColor = color
            val drawables = compoundDrawablesRelative
            for (drawable in drawables) {
                if (drawable == null) continue
                ViewHelper.tintDrawable(drawable, color)
            }
        }
    }

    fun setEventsIcon(@DrawableRes drawableRes: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableRes)!!
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        drawable.setBounds(0, 0, width / 2, height / 2)
        val sd = ScaleDrawable(drawable, Gravity.CENTER, 0.6f, 0.6f)
        sd.level = 8000
        ViewHelper.tintDrawable(drawable, ViewHelper.getTertiaryTextColor(context))
        setCompoundDrawablesWithIntrinsicBounds(sd, null, null, null)
    }

    init {
        init(context, attrs)
    }
}