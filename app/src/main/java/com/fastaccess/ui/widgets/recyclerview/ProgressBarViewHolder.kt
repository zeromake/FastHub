package com.fastaccess.ui.widgets.recyclerview

import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by kosh on 03/08/2017.
 */
class ProgressBarViewHolder private constructor(itemView: View) : BaseViewHolder<Any>(itemView) {
    override fun bind(t: Any) {}

    companion object {
        fun newInstance(viewGroup: ViewGroup?): ProgressBarViewHolder {
            return ProgressBarViewHolder(getView(viewGroup!!, R.layout.progress_layout))
        }
    }
}