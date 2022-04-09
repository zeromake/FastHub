package com.fastaccess.ui.modules.notification.unread

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.GroupedNotificationModel
import com.fastaccess.data.dao.GroupedNotificationModel.Companion.onlyNotifications
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.entity.Notification
import com.fastaccess.data.entity.dao.NotificationDao
import com.fastaccess.helper.ParseDateFormat.Companion.lastWeekDate
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.PrefGetter.isMarkAsReadEnabled
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.provider.tasks.notification.ReadNotificationService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by Kosh on 25 Apr 2017, 3:55 PM
 */
class UnreadNotificationsPresenter : BasePresenter<UnreadNotificationMvp.View>(),
    UnreadNotificationMvp.Presenter {
    override val notifications: MutableList<GroupedNotificationModel> = mutableListOf()
    override fun onItemClick(position: Int, v: View?, item: GroupedNotificationModel) {
        v ?: return
        val notification = item.notification
        if (v.id == R.id.markAsRead) {
            if (notification!!.unread) markAsRead(position, v, notification)
        } else {
            if (notification!!.subject != null && notification.subject!!.url != null) {
                if (notification.unread && !isMarkAsReadEnabled) {
                    markAsRead(position, v, notification)
                }
                if (view != null) view!!.onClick(notification.subject!!.url!!)
            }
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: GroupedNotificationModel) {}
    override fun onWorkOffline() {
        if (notifications.isEmpty()) {
            manageDisposable(RxHelper.getObservable(
                NotificationDao.getUnreadNotifications().toObservable()
            )
                .flatMap { notifications: List<Notification?>? ->
                    Observable.just(
                        onlyNotifications(notifications?.filterNotNull() ?: listOf())
                    )
                }
                .subscribe { models ->
                    sendToView { view ->
                        view?.onNotifyAdapter(
                            models
                        )
                    }
                })
        } else {
            sendToView { it?.hideProgress() }
        }
    }

    override fun onMarkAllAsRead(data: List<GroupedNotificationModel>) {
        manageDisposable(RxHelper.getObservable(Observable.fromIterable(data))
            .filter { group: GroupedNotificationModel? -> group != null && group.type == GroupedNotificationModel.ROW }
            .filter { group: GroupedNotificationModel? -> group!!.notification != null && group.notification!!.unread }
            .map { it.notification!! }
            .subscribe({ notification: Notification ->
                notification.unread = false
                manageObservable(NotificationDao.save(notification).toObservable())
                sendToView { view ->
                    view?.onReadNotification(
                        notification
                    )
                }
            }) { throwable: Throwable? ->
                onError(
                    throwable!!
                )
            })
    }

    override fun onCallApi() {
        val observable = RestProvider.getNotificationService(PrefGetter.isEnterprise)
            .getNotifications(lastWeekDate).flatMap { response: Pageable<Notification> ->
                val items = response.items ?: listOf()
                manageObservable(
                    NotificationDao.save(items).toObservable()
                )
                Observable.just(onlyNotifications(items))
            }
        makeRestCall(
            observable
        ) { response ->
            sendToView { view ->
                view?.onNotifyAdapter(
                    response!!
                )
            }
        }
    }

    private fun markAsRead(position: Int, v: View, item: Notification?) {
        item!!.unread = false
        manageObservable(NotificationDao.save(item).toObservable())
        sendToView { view -> view?.onRemove(position) }
        ReadNotificationService.start(v.context, item.id)
    }
}