package com.fastaccess.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import android.view.inputmethod.EditorInfo
import com.fastaccess.helper.TypeFaceHelper

/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
class FontAutoCompleteEditText : AppCompatAutoCompleteTextView {
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
        if (isInEditMode) return
        inputType =
            inputType or EditorInfo.IME_FLAG_NO_EXTRACT_UI or EditorInfo.IME_FLAG_NO_FULLSCREEN
        imeOptions = imeOptions or EditorInfo.IME_FLAG_NO_FULLSCREEN
        TypeFaceHelper.applyTypeface(this)
    }
}