package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.ui.adapter.MilestonesAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */
class MilestonesViewHolder private constructor(
    itemView: View,
    adapter: MilestonesAdapter
) : BaseViewHolder<MilestoneModel>(itemView, adapter) {
    val title: FontTextView? = itemView.findViewById(R.id.title)
    val date: FontTextView? = itemView.findViewById(R.id.date)
    val notificationTitle: FontTextView? = itemView.findViewById(R.id.notificationTitle)
    override fun bind(t: MilestoneModel) {
        title!!.text = t.title
        notificationTitle!!.text = t.description
        if (t.dueOn != null) {
            date!!.text = getTimeAgo(t.dueOn)
        } else if (t.createdAt != null) {
            date!!.text = getTimeAgo(t.createdAt)
        }
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(
            viewGroup: ViewGroup,
            adapter: MilestonesAdapter
        ): MilestonesViewHolder {
            return MilestonesViewHolder(getView(viewGroup, R.layout.milestone_row_item), adapter)
        }
    }
}