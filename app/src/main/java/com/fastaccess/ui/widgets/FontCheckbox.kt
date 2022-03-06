package com.fastaccess.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.fastaccess.helper.TypeFaceHelper

/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
class FontCheckbox : AppCompatCheckBox {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        if (isInEditMode) return
        TypeFaceHelper.applyTypeface(this)
    }
}