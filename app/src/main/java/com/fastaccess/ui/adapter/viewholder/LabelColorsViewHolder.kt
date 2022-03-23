package com.fastaccess.ui.adapter.viewholder

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.helper.ViewHelper.generateTextColor
import com.fastaccess.ui.adapter.LabelColorsAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created by Kosh on 02 Apr 2017, 5:19 PM
 */
class LabelColorsViewHolder private constructor(
    itemView: View,
    adapter: LabelColorsAdapter
) : BaseViewHolder<String>(itemView, adapter) {
    var color: FontTextView? = itemView.findViewById(R.id.color)
    override fun bind(t: String) {
        val labelColor = Color.parseColor(t)
        itemView.setBackgroundColor(labelColor)
        color!!.setTextColor(generateTextColor(labelColor))
        color!!.text = t
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(
            parent: ViewGroup?,
            adapter: LabelColorsAdapter
        ): LabelColorsViewHolder {
            return LabelColorsViewHolder(getView(parent!!, R.layout.simple_color_row_item), adapter)
        }
    }
}