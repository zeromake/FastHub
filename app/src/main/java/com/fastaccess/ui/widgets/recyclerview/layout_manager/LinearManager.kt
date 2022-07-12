package com.fastaccess.ui.widgets.recyclerview.layout_manager

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.fastaccess.provider.crash.Report

/**
 * Created by Kosh on 17 May 2016, 10:02 PM
 */
class LinearManager : LinearLayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Exception) {
            Report.reportCatchException(e)
        }
    }

    override fun onMeasure(
        recycler: Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        try {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
        } catch (e: Exception) {
            Report.reportCatchException(e)
        }
    }
}