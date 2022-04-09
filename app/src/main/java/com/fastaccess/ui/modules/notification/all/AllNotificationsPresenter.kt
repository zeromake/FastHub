package com.fastaccess.ui.modules.notification.all

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.GroupedNotificationModel
import com.fastaccess.data.dao.GroupedNotificationModel.Companion.construct
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.entity.Notification
import com.fastaccess.data.entity.Repo
import com.fastaccess.data.entity.dao.NotificationDao
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.PrefGetter.isMarkAsReadEnabled
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.provider.tasks.notification.ReadNotificationService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import io.reactivex.Observable

/**
 * Created by Kosh on 20 Feb 2017, 8:46 PM
 */
class AllNotificationsPresenter : BasePresenter<AllNotificationsMvp.View>(),
    AllNotificationsMvp.Presenter {
    override val notifications: MutableList<GroupedNotificationModel> = mutableListOf()
    override fun onItemClick(position: Int, v: View?, item: GroupedNotificationModel) {
        v ?: return
        if (item.type == GroupedNotificationModel.ROW) {
            item.notification?.let { notification ->
                if (v.id == R.id.markAsRead) {
                    if (notification.unread && !isMarkAsReadEnabled) {
                        markAsRead(position, v, notification)
                    }
                } else {
                    if (notification.subject != null && notification.subject!!.url != null) {
                        if (notification.unread && !isMarkAsReadEnabled) {
                            markAsRead(position, v, notification)
                        }
                        if (view != null) view!!.onClick(notification.subject!!.url!!)
                    }
                }
            }

        } else {
            val repo = item.repo ?: return
            if (v.id == R.id.markAsRead) {
                view!!.onMarkAllByRepo(repo)
            } else {
                RepoPagerActivity.startRepoPager(v.context, NameParser(repo.url))
            }
        }
    }

    private fun markAsRead(position: Int, v: View, item: Notification) {
        item.unread = false
        manageObservable(NotificationDao.save(item).toObservable())
        sendToView { view ->
            view?.onUpdateReadState(
                GroupedNotificationModel(
                    item
                ), position
            )
        }
        ReadNotificationService.start(v.context, item.id)
    }

    override fun onItemLongClick(position: Int, v: View?, item: GroupedNotificationModel) {}
    override fun onError(throwable: Throwable) {
        onWorkOffline()
        super.onError(throwable)
    }

    override fun onWorkOffline() {
        if (notifications.isEmpty()) {
            val disposable = RxHelper.getObservable(
                NotificationDao.getAllNotifications().toObservable()
            ).flatMap { notifications ->
                Observable.just(
                    construct(notifications)
                )
            }.subscribe { models ->
                sendToView { view ->
                    view?.onNotifyAdapter(models)
                }
            }
            manageDisposable(disposable)
        } else {
            sendToView { it?.hideProgress() }
        }
    }

    override fun onCallApi() {
        val observable = RestProvider.getNotificationService(PrefGetter.isEnterprise)
            .allNotifications.flatMap { response: Pageable<Notification> ->
                val items = response.items ?: listOf()
                manageObservable(NotificationDao.save(items).toObservable())
                if (items.isNotEmpty()) {
                    return@flatMap Observable.just(construct(items))
                }
                Observable.empty()
            }
        makeRestCall(observable.doOnComplete {
            sendToView { view ->
                view?.hideProgress()
            }
        }) { response ->
            sendToView { view ->
                view?.onNotifyAdapter(response)
            }
        }
    }

    override fun onMarkAllAsRead(data: List<GroupedNotificationModel>) {
        val disposable = RxHelper.getObservable(Observable.fromIterable(data))
            .filter { group: GroupedNotificationModel -> group.type == GroupedNotificationModel.ROW }
            .filter { group: GroupedNotificationModel -> group.notification != null && group.notification!!.unread }
            .map(GroupedNotificationModel::notification)
            .subscribe({
                it?.let { notification ->
                    notification.unread = false
                    manageObservable(NotificationDao.save(notification).toObservable())
                    sendToView { view ->
                        view?.onReadNotification(
                            notification
                        )
                    }
                }
            }) { throwable: Throwable -> onError(throwable) }
        manageDisposable(disposable)
    }

    override fun onMarkReadByRepo(data: List<GroupedNotificationModel>, repo: Repo) {
        val disposable = RxHelper.getObservable(Observable.fromIterable(data))
            .filter { group: GroupedNotificationModel -> group.type == GroupedNotificationModel.ROW }
            .filter { group: GroupedNotificationModel -> group.notification != null && group.notification!!.unread }
            .filter { group: GroupedNotificationModel ->
                group.notification!!.repository!!.fullName.equals(
                    repo.fullName,
                    ignoreCase = true
                )
            }
            .map { it.notification!! }
            .subscribe({ notification: Notification ->
                notification.unread = false
                manageObservable(NotificationDao.save(notification).toObservable())
                sendToView { view ->
                    view?.onReadNotification(
                        notification
                    )
                }
            }) { throwable: Throwable -> onError(throwable) }
        manageDisposable(disposable)
    }
}