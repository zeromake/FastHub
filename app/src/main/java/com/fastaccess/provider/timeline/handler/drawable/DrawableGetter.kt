package com.fastaccess.provider.timeline.handler.drawable

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fastaccess.R
import java.lang.ref.WeakReference

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */
class DrawableGetter(tv: TextView, width: Int) : ImageGetter, Drawable.Callback {
    private val container: WeakReference<TextView?>?
    private val cachedTargets: MutableSet<GlideDrawableTarget>?
    private val width: Int
    override fun getDrawable(url: String): Drawable {
        val urlDrawable = UrlDrawable()
        if (container?.get() != null) {
            val context = container.get()!!.context
            var load = Glide.with(context)
                .load(url)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image))
                .dontAnimate()
            val target = GlideDrawableTarget(urlDrawable, container, width)
            load = load.override(width, width / 2)
            load.into(target)
            cachedTargets!!.add(target)
        }
        return urlDrawable
    }

    override fun invalidateDrawable(drawable: Drawable) {
        if (container?.get() != null) {
            container.get()!!.invalidate()
        }
    }

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, l: Long) {}
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) {}
    fun clear(context: Context, drawableGetter: DrawableGetter) {
        if (drawableGetter.cachedTargets != null) {
            for (target in drawableGetter.cachedTargets) {
                Glide.with(context).clear(target)
            }
        }
    }

    init {
        tv.setTag(R.id.drawable_callback, this)
        container = WeakReference(tv)
        cachedTargets = HashSet()
        this.width = width
    }
}