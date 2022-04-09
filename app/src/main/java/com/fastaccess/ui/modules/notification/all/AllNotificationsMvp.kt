package com.fastaccess.ui.modules.notification.all

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread
import com.fastaccess.data.dao.GroupedNotificationModel
import com.fastaccess.data.entity.Notification
import com.fastaccess.data.entity.Repo
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Kosh on 19 Feb 2017, 7:53 PM
 */
interface AllNotificationsMvp {
    interface View : FAView, OnRefreshListener {
        @CallOnMainThread
        fun onNotifyAdapter(items: List<GroupedNotificationModel>)
        fun onUpdateReadState(item: GroupedNotificationModel, position: Int)
        fun onClick(url: String)
        fun onReadNotification(notification: Notification)
        fun onMarkAllByRepo(repo: Repo)
        fun onNotifyNotificationChanged(notification: GroupedNotificationModel)
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<GroupedNotificationModel> {
        fun onWorkOffline()
        val notifications: MutableList<GroupedNotificationModel>
        fun onCallApi()
        fun onMarkAllAsRead(data: List<GroupedNotificationModel>)
        fun onMarkReadByRepo(data: List<GroupedNotificationModel>, repo: Repo)
    }
}