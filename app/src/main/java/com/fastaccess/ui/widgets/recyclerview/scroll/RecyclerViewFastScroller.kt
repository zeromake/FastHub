package com.fastaccess.ui.widgets.recyclerview.scroll

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fastaccess.R
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.Logger.e
import com.fastaccess.provider.crash.Report
import com.google.android.material.appbar.AppBarLayout
import it.sephiroth.android.library.bottomnavigation.BottomNavigation

open class RecyclerViewFastScroller : FrameLayout {
    private var scrollerView: ImageView? = null
    private val scrollTop: ImageButton? = null
    private var mHeight = 0
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var appBarLayout: AppBarLayout? = null
    private var bottomNavigation: BottomNavigation? = null
    private var toggled = false
    private var registeredObserver = false

    constructor(context: Context?) : super(context!!) {
        init()
    }

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHeight = h
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x < scrollerView!!.x - scrollerView!!.paddingStart) return false
                scrollerView!!.isSelected = true
                hideAppbar()
                val y = event.y
                setScrollerHeight(y)
                setRecyclerViewPosition(y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.y
                setScrollerHeight(y)
                setRecyclerViewPosition(y)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                scrollerView!!.isSelected = false
                showAppbar()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDetachedFromWindow() {
        if (recyclerView != null) {
            recyclerView!!.removeOnScrollListener(onScrollListener)
            safelyUnregisterObserver()
        }
        appBarLayout = null
        bottomNavigation = null
        super.onDetachedFromWindow()
    }

    private fun safelyUnregisterObserver() {
        try { // rare case
            if (registeredObserver && recyclerView!!.adapter != null) {
                recyclerView!!.adapter!!.unregisterAdapterDataObserver(observer)
            }
        } catch (e: Exception) {
            Report.reportCatchException(e)
        }
    }

    protected fun init() {
        visibility = GONE
        clipChildren = false
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.fastscroller_layout, this)
        scrollerView = findViewById(R.id.fast_scroller_handle)
        visibility = VISIBLE
        val activity = ActivityHelper.getActivity(context)
        if (activity != null) {
            appBarLayout = activity.findViewById(R.id.appbar)
            bottomNavigation = activity.findViewById(R.id.bottomNavigation)
        }
    }

    private fun hideAppbar() {
        if (!toggled) {
            if (appBarLayout != null) {
                appBarLayout!!.setExpanded(false, true)
            }
            if (bottomNavigation != null) {
                bottomNavigation!!.setExpanded(expanded = false, animate = true)
            }
            toggled = true
        }
    }

    private fun showAppbar() {
        if (toggled) {
            if (scrollerView!!.y == 0f) {
                if (appBarLayout != null) {
                    appBarLayout!!.setExpanded(true, true)
                }
                if (bottomNavigation != null) {
                    bottomNavigation!!.setExpanded(expanded = true, animate = true)
                }
                toggled = false
            }
        }
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        if (this.recyclerView == null) {
            this.recyclerView = recyclerView
            layoutManager = recyclerView.layoutManager
            this.recyclerView!!.addOnScrollListener(onScrollListener)
            if (recyclerView.adapter != null && !registeredObserver) {
                recyclerView.adapter!!.registerAdapterDataObserver(observer)
                registeredObserver = true
            }
            hideShow()
            initScrollHeight()
        }
    }

    private fun initScrollHeight() {
        if (recyclerView!!.computeVerticalScrollOffset() == 0) {
            recyclerView!!.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    recyclerView!!.viewTreeObserver.removeOnPreDrawListener(this)
                    iniHeight()
                    return true
                }
            })
        } else {
            iniHeight()
        }
    }

    protected fun iniHeight() {
        if (scrollerView!!.isSelected) return
        val verticalScrollOffset = recyclerView!!.computeVerticalScrollOffset()
        val verticalScrollRange = computeVerticalScrollRange()
        val proportion = verticalScrollOffset.toFloat() / (verticalScrollRange.toFloat() - mHeight)
        setScrollerHeight(mHeight * proportion)
    }

    private fun setRecyclerViewPosition(y: Float) {
        e(y)
        if (recyclerView != null) {
            val itemCount = recyclerView!!.adapter!!.itemCount
            val proportion: Float = when {
                scrollerView!!.y == 0f -> {
                    0f
                }
                scrollerView!!.y + scrollerView!!.height >= mHeight - TRACK_SNAP_RANGE -> {
                    1f
                }
                else -> {
                    y / mHeight.toFloat()
                }
            }
            val targetPos =
                getValueInRange(itemCount - 1, (proportion * itemCount.toFloat()).toInt())
            when (layoutManager) {
                is StaggeredGridLayoutManager -> {
                    (layoutManager as StaggeredGridLayoutManager).scrollToPositionWithOffset(
                        targetPos,
                        0
                    )
                }
                is GridLayoutManager -> {
                    (layoutManager as GridLayoutManager).scrollToPositionWithOffset(targetPos, 0)
                }
                else -> {
                    (layoutManager as LinearLayoutManager?)!!.scrollToPositionWithOffset(targetPos, 0)
                }
            }
        }
    }

    private fun setScrollerHeight(y: Float) {
        val handleHeight = scrollerView!!.height
        scrollerView!!.y = getValueInRange(
            mHeight - handleHeight,
            (y - handleHeight / 2).toInt()
        ).toFloat()
    }

    private val onScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (scrollerView!!.isSelected) return
                val verticalScrollOffset = recyclerView.computeVerticalScrollOffset()
                val verticalScrollRange = recyclerView.computeVerticalScrollRange()
                val proportion =
                    verticalScrollOffset.toFloat() / (verticalScrollRange.toFloat() - mHeight)
                setScrollerHeight(mHeight * proportion)
            }
        }
    private val observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            hideShow()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            hideShow()
        }

        override fun onChanged() {
            super.onChanged()
            hideShow()
        }
    }

    protected fun hideShow() {
        visibility = if (recyclerView != null && recyclerView!!.adapter != null) {
            if (recyclerView!!.adapter!!.itemCount > 10) VISIBLE else GONE
        } else {
            GONE
        }
    }

    companion object {
        private const val TRACK_SNAP_RANGE = 5
        private fun getValueInRange(max: Int, value: Int): Int {
            return 0.coerceAtLeast(value).coerceAtMost(max)
        }
    }
}