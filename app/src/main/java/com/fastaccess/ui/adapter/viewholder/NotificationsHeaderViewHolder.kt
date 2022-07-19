package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import com.fastaccess.R
import com.fastaccess.data.dao.GroupedNotificationModel
import com.fastaccess.ui.adapter.NotificationsAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */
class NotificationsHeaderViewHolder private constructor(
    itemView: View,
    adapter: NotificationsAdapter
) : BaseViewHolder<GroupedNotificationModel>(itemView, adapter) {
    var headerTitle: FontTextView? = itemView.findViewById(R.id.headerTitle)
    var markAsRead: AppCompatImageButton? = itemView.findViewById(R.id.markAsRead)
    override fun bind(t: GroupedNotificationModel) {
        val repo = t.repo
        if (repo != null && headerTitle != null) {
            headerTitle!!.text = repo.fullName
        }
    }

    companion object {
        fun newInstance(
            viewGroup: ViewGroup,
            adapter: NotificationsAdapter
        ): NotificationsHeaderViewHolder {
            return NotificationsHeaderViewHolder(
                getView(
                    viewGroup,
                    R.layout.notification_header_row_item
                ), adapter
            )
        }
    }

    init {
        markAsRead!!.setOnClickListener(this)
    }
}