package com.fastaccess.ui.widgets.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fastaccess.R
import com.fastaccess.helper.ViewHelper
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.BottomPaddingDecoration.Companion.with

/**
 * Created by Kosh on 9/24/2015. copyrights are reserved
 *
 *
 * recyclerview which will showParentOrSelf/showParentOrSelf itself base on adapter
 */
class DynamicRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    private var emptyView: StateLayout? = null
    private var parentView: View? = null
    private var bottomPaddingDecoration: BottomPaddingDecoration? = null
    private val observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            showEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            showEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            showEmptyView()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        if (isInEditMode) return
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer)
            observer.onChanged()
        }
    }

    fun removeBottomDecoration() {
        if (bottomPaddingDecoration != null) {
            removeItemDecoration(bottomPaddingDecoration!!)
            bottomPaddingDecoration = null
        }
    }

    fun addDecoration() {
        bottomPaddingDecoration = with(context)
        addItemDecoration(bottomPaddingDecoration!!)
    }

    private fun showEmptyView() {
        val adapter = adapter
        if (adapter != null) {
            if (emptyView != null) {
                if (adapter.itemCount == 0) {
                    showParentOrSelf(false)
                } else {
                    showParentOrSelf(true)
                }
            }
        } else {
            if (emptyView != null) {
                showParentOrSelf(false)
            }
        }
    }

    private fun showParentOrSelf(showRecyclerView: Boolean) {
        if (parentView != null) parentView!!.visibility = VISIBLE
        visibility = VISIBLE
        emptyView!!.visibility = if (!showRecyclerView) VISIBLE else GONE
    }

    fun setEmptyView(emptyView: StateLayout, parentView: View?) {
        this.emptyView = emptyView
        this.parentView = parentView
        showEmptyView()
    }

    fun setEmptyView(emptyView: StateLayout) {
        setEmptyView(emptyView, null)
    }

    fun hideProgress(view: StateLayout) {
        view.hideProgress()
    }

    fun showProgress(view: StateLayout) {
        view.showProgress()
    }

    fun addKeyLineDivider() {
        if (canAddDivider()) {
            val resources = resources
            addItemDecoration(
                InsetDividerDecoration<ViewHolder>(
                    resources.getDimensionPixelSize(R.dimen.divider_height),
                    resources.getDimensionPixelSize(R.dimen.keyline_2),
                    ViewHelper.getListDivider(context)
                )
            )
        }
    }

    fun addDivider() {
        if (canAddDivider()) {
            val resources = resources
            addItemDecoration(
                InsetDividerDecoration<ViewHolder>(
                    resources.getDimensionPixelSize(R.dimen.divider_height), 0,
                    ViewHelper.getListDivider(context)
                )
            )
        }
    }

    fun addNormalSpacingDivider() {
        addDivider()
    }

    fun <P : ViewHolder> addDivider(toDivide: Class<P>) {
        if (canAddDivider()) {
            val resources = resources
            addItemDecoration(
                InsetDividerDecoration(
                    resources.getDimensionPixelSize(R.dimen.divider_height), 0,
                    ViewHelper.getListDivider(context), toDivide
                )
            )
        }
    }

    private fun canAddDivider(): Boolean {
        if (layoutManager != null) {
            when (layoutManager) {
                is GridLayoutManager -> {
                    return (layoutManager as GridLayoutManager?)!!.spanCount == 1
                }
                is LinearLayoutManager -> {
                    return true
                }
                is StaggeredGridLayoutManager -> {
                    return (layoutManager as StaggeredGridLayoutManager?)!!.spanCount == 1
                }
            }
        }
        return false
    }
}