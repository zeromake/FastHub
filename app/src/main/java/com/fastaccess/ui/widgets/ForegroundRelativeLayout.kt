package com.fastaccess.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import android.graphics.drawable.Drawable
import android.graphics.Canvas
import android.util.AttributeSet
import com.fastaccess.R
import android.view.ViewOutlineProvider

/**
 * An extension to [RelativeLayout] which has a foreground drawable.
 */
class ForegroundRelativeLayout @SuppressLint("CustomViewStyleable") constructor(
    context: Context,
    attrs: AttributeSet?
) : RelativeLayout(context, attrs) {
    private var foreground: Drawable? = null
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (foreground != null) {
            foreground!!.setBounds(0, 0, w, h)
        }
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === foreground
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (foreground != null) foreground!!.jumpToCurrentState()
    }

    override fun drawableStateChanged() {
        if (isInEditMode) return
        super.drawableStateChanged()
        if (foreground != null && foreground!!.isStateful) {
            foreground!!.state = drawableState
        }
    }

    override fun getForeground(): Drawable {
        return foreground!!
    }

    override fun setForeground(drawable: Drawable) {
        if (foreground !== drawable) {
            if (foreground != null) {
                foreground!!.callback = null
                unscheduleDrawable(foreground)
            }
            foreground = drawable
            if (foreground != null) {
                foreground!!.setBounds(left, top, right, bottom)
                setWillNotDraw(false)
                foreground!!.callback = this
                if (foreground!!.isStateful) {
                    foreground!!.state = drawableState
                }
            } else {
                setWillNotDraw(true)
            }
            invalidate()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (foreground != null) {
            foreground!!.draw(canvas)
        }
    }

    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        if (foreground != null) {
            foreground!!.setHotspot(x, y)
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundView)
        val d = a.getDrawable(R.styleable.ForegroundView_android_foreground)
        d?.let { setForeground(it) }
        a.recycle()
        outlineProvider = ViewOutlineProvider.BOUNDS
    }
}