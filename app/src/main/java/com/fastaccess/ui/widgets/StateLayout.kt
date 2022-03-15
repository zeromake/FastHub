package com.fastaccess.ui.widgets

import android.content.Context
import androidx.core.widget.NestedScrollView
import com.fastaccess.R
import androidx.annotation.StringRes
import android.os.Parcelable
import android.util.AttributeSet
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Kosh on 20 Nov 2016, 12:21 AM
 */
open class StateLayout : NestedScrollView {
    private var onReloadListener: OnClickListener? = null

    lateinit var emptyText: FontTextView
    lateinit var reload: FontButton

    @State
    var layoutState = HIDDEN

    @State
    var emptyTextValue: String? = null

    @State
    var showReload = true

    internal fun onReload() {
        if (onReloadListener != null) {
            onReloadListener!!.onClick(reload)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        inflate(context, R.layout.empty_layout, this)
        if (isInEditMode) return
        this.emptyText = this.findViewById(R.id.empty_text)
        this.reload = this.findViewById(R.id.reload)
        this.reload.setOnThrottleClickListener {
            this.onReload()
        }

        emptyText.freezesText = true
    }

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
    }

    fun showProgress() {
        layoutState = SHOW_PROGRESS_STATE
        visibility = VISIBLE
        emptyText.visibility = GONE
        reload.visibility = GONE
    }

    fun hideProgress() {
        layoutState = HIDE_PROGRESS_STATE
        emptyText.visibility = VISIBLE
        reload.visibility = VISIBLE
        visibility = GONE
    }

    fun hideReload() {
        layoutState = HIDE_RELOAD_STATE
        reload.visibility = GONE
        emptyText.visibility = GONE
        visibility = GONE
    }

    fun showReload(adapterCount: Int) {
        showReload = adapterCount == 0
        showReload()
    }

    protected fun showReload() {
        hideProgress()
        if (showReload) {
            layoutState = SHOW_RELOAD_STATE
            reload.visibility = VISIBLE
            emptyText.visibility = VISIBLE
            visibility = VISIBLE
        }
    }

    fun setEmptyText(@StringRes resId: Int) {
        setEmptyText(resources.getString(resId))
    }

    fun setEmptyText(text: String) {
        emptyTextValue = text
        emptyText.text = emptyTextValue
    }

    fun showEmptyState() {
        hideProgress()
        hideReload()
        visibility = VISIBLE
        emptyText.visibility = VISIBLE
        layoutState = SHOW_EMPTY_STATE // last so it override visibility state.
    }

    fun setOnReloadListener(onReloadListener: OnClickListener?) {
        this.onReloadListener = onReloadListener
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        layoutState = if (visibility == GONE || visibility == INVISIBLE) {
            HIDDEN
        } else {
            SHOWN
        }
    }


    override fun onDetachedFromWindow() {
        onReloadListener = null
        super.onDetachedFromWindow()
    }

    public override fun onSaveInstanceState(): Parcelable? {
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState())
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state))
        onHandleLayoutState()
    }

    private fun onHandleLayoutState() {
        setEmptyText(emptyTextValue!!)
        when (layoutState) {
            SHOW_PROGRESS_STATE -> showProgress()
            HIDE_PROGRESS_STATE -> hideProgress()
            HIDE_RELOAD_STATE -> hideReload()
            SHOW_RELOAD_STATE -> showReload()
            HIDDEN -> visibility = GONE
            SHOW_EMPTY_STATE -> showEmptyState()
            SHOWN -> {
                visibility = VISIBLE
                showReload()
            }
        }
    }

    companion object {
        private const val SHOW_PROGRESS_STATE = 1
        private const val HIDE_PROGRESS_STATE = 2
        private const val HIDE_RELOAD_STATE = 3
        private const val SHOW_RELOAD_STATE = 4
        private const val SHOW_EMPTY_STATE = 7
        private const val HIDDEN = 5
        private const val SHOWN = 6
    }
}