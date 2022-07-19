package com.fastaccess.ui.modules.notification.fasthub

import com.fastaccess.data.entity.FastHubNotification
import com.fastaccess.data.entity.dao.FastHubNotificationDao
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 19.11.17.
 */
class FastHubNotificationsPresenter : BasePresenter<FastHubNotificationsMvp.View>(),
    FastHubNotificationsMvp.Presenter {
    private val data = mutableListOf<FastHubNotification>()

    override fun getData(): List<FastHubNotification> = data

    override fun load() {
        manageObservable(
            FastHubNotificationDao.getNotifications()
                .toList()
                .toObservable()
                .doOnNext {
                    sendToView { v -> v.notifyAdapter(it) }
                })
    }
}