package com.fastaccess.provider.timeline.handler.drawable

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.gif.GifDrawable

internal class UrlDrawable: BitmapDrawable(), Drawable.Callback {
    private var drawable: Drawable? = null
    override fun draw(canvas: Canvas) {
        if (drawable != null) {
            drawable!!.draw(canvas)
            if (drawable is GifDrawable) {
                if (!(drawable as GifDrawable).isRunning) {
                    (drawable as GifDrawable).start()
                }
            }
        }
    }

    fun getDrawable(): Drawable? {
        return drawable
    }

    fun setDrawable(drawable: Drawable?) {
        if (this.drawable != null) {
            this.drawable!!.callback = null
        }
        if (drawable == null) {
            this.drawable = null
        } else {
            drawable.callback = this
            this.drawable = drawable
        }
    }

    override fun invalidateDrawable(who: Drawable) {
        if (callback != null) {
            callback!!.invalidateDrawable(who)
        }
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        if (callback != null) {
            callback!!.scheduleDrawable(who, what, `when`)
        }
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        if (callback != null) {
            callback!!.unscheduleDrawable(who, what)
        }
    }
}