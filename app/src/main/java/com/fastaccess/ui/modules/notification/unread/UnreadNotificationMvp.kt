package com.fastaccess.ui.modules.notification.unread

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread
import com.fastaccess.data.dao.GroupedNotificationModel
import com.fastaccess.data.entity.Notification
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Kosh on 25 Apr 2017, 3:51 PM
 */
interface UnreadNotificationMvp {
    interface View : FAView, OnRefreshListener {
        @CallOnMainThread
        fun onNotifyAdapter(items: List<GroupedNotificationModel>)
        fun onRemove(position: Int)
        fun onReadNotification(notification: Notification)
        fun onClick(url: String)
        fun onNotifyNotificationChanged(notification: GroupedNotificationModel)
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<GroupedNotificationModel> {
        fun onWorkOffline()
        val notifications: MutableList<GroupedNotificationModel>
        fun onMarkAllAsRead(data: List<GroupedNotificationModel>)
        fun onCallApi()
    }
}