package com.fastaccess.ui.widgets.recyclerview.scroll

import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Created by Kosh on 8/2/2015. copyrights are reserved @
 */
abstract class InfiniteScroll : RecyclerView.OnScrollListener() {
    private var visibleThreshold = 3
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 0
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: BaseRecyclerAdapter<*, *, *>? = null
    private var newlyAdded = true
    private fun initLayoutManager(layoutManager: RecyclerView.LayoutManager?) {
        this.layoutManager = layoutManager
        if (layoutManager is GridLayoutManager) {
            visibleThreshold *= layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            visibleThreshold *= layoutManager.spanCount
        }
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (newlyAdded) {
            newlyAdded = false
            return
        }
        onScrolled(dy > 0)
        if (layoutManager == null) {
            initLayoutManager(recyclerView.layoutManager)
        }
        if (adapter == null) {
            if (recyclerView.adapter is BaseRecyclerAdapter<*, *, *>) {
                adapter = recyclerView.adapter as BaseRecyclerAdapter<*, *, *>?
            }
        }
        if (adapter != null && adapter!!.isProgressAdded) return
        var lastVisibleItemPosition = 0
        val totalItemCount = layoutManager!!.itemCount
        when (layoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions =
                    (layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager -> {
                lastVisibleItemPosition =
                    (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                lastVisibleItemPosition =
                    (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
        }
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }
        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            val isCallingApi = onLoadMore(currentPage, totalItemCount)
            loading = true
            if (adapter != null && isCallingApi) {
                adapter!!.addProgress()
            }
        }
    }

    fun reset() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    fun initialize(page: Int, previousTotal: Int) {
        currentPage = page
        previousTotalItemCount = previousTotal
        loading = true
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int): Boolean
    open fun onScrolled(isUp: Boolean) {}
}