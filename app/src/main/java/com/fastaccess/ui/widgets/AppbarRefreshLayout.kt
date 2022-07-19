package com.fastaccess.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R

/**
 * Created by kosh on 7/30/2015. CopyRights @
 */
class AppbarRefreshLayout : SwipeRefreshLayout {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setColorSchemeResources(
            R.color.material_amber_700,
            R.color.material_blue_700,
            R.color.material_purple_700,
            R.color.material_lime_700
        )
    }
}