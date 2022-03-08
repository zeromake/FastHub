package com.fastaccess.ui.adapter

import android.view.View
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.data.dao.GroupedNotificationModel
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import android.view.ViewGroup
import com.fastaccess.ui.adapter.viewholder.NotificationsHeaderViewHolder
import com.fastaccess.ui.adapter.viewholder.NotificationsViewHolder
import com.fastaccess.R
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */
class NotificationsAdapter :
    BaseRecyclerAdapter<GroupedNotificationModel?, BaseViewHolder<*>?, BaseViewHolder.OnItemClickListener<GroupedNotificationModel?>?> {
    private var showUnreadState: Boolean
    private var hideClear = false

    constructor(
        eventsModels: List<GroupedNotificationModel?>,
        showUnreadState: Boolean
    ) : super(eventsModels) {
        this.showUnreadState = showUnreadState
    }

    constructor(
        eventsModels: List<GroupedNotificationModel?>,
        showUnreadState: Boolean,
        hideClear: Boolean
    ) : super(eventsModels) {
        this.showUnreadState = showUnreadState
        this.hideClear = hideClear
    }

    override fun viewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return if (viewType == GroupedNotificationModel.HEADER) {
            NotificationsHeaderViewHolder.newInstance(parent, this)
        } else {
            NotificationsViewHolder.newInstance(parent, this, showUnreadState)
        }
    }

    override fun onBindView(holder: BaseViewHolder<*>?, position: Int) {
        if (getItemViewType(position) == GroupedNotificationModel.HEADER) {
            (holder as NotificationsHeaderViewHolder).bind(getItem(position)!!)
            if (hideClear) if (getItem(
                    (position + 1).coerceAtMost(itemCount - 1)
                )!!.notification!!.isUnread
            ) {
                holder.itemView.findViewById<View>(R.id.markAsRead).visibility =
                    View.VISIBLE
            }
        } else {
            (holder as NotificationsViewHolder).bind(getItem(position)!!)
        }
        if (getItem(position)!!.type == GroupedNotificationModel.HEADER) {
            val layoutParams =
                holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.isFullSpan = true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.type
    }
}