package com.fastaccess.ui.modules.notification.all

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.GroupedNotificationModel
import com.fastaccess.data.dao.GroupedNotificationModel.Companion.construct
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.model.Notification
import com.fastaccess.data.dao.model.Repo
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
    override fun onItemClick(position: Int, v: View, model: GroupedNotificationModel?) {
        if (view == null) return
        model ?: return
        if (model.type == GroupedNotificationModel.ROW) {
            model.notification?.let { item ->
                if (v.id == R.id.markAsRead) {
                    if (item.isUnread && !isMarkAsReadEnabled) {
                        markAsRead(position, v, item)
                    }
                } else if (v.id == R.id.unsubsribe) {
                    item.isUnread = false
                    manageDisposable(item.save(item))
                    sendToView { view ->
                        view?.onUpdateReadState(
                            GroupedNotificationModel(
                                item
                            ), position
                        )
                    }
                    ReadNotificationService.unSubscribe(v.context, item.id)
                } else {
                    if (item.subject != null && item.subject.url != null) {
                        if (item.isUnread && !isMarkAsReadEnabled) {
                            markAsRead(position, v, item)
                        }
                        if (view != null) view!!.onClick(item.subject.url!!)
                    }
                }
            }

        } else {
            val repo = model.repo ?: return
            if (v.id == R.id.markAsRead) {
                view!!.onMarkAllByRepo(repo)
            } else {
                RepoPagerActivity.startRepoPager(v.context, NameParser(repo.url))
            }
        }
    }

    private fun markAsRead(position: Int, v: View, item: Notification) {
        item.isUnread = false
        manageDisposable(item.save(item))
        sendToView { view ->
            view?.onUpdateReadState(
                GroupedNotificationModel(
                    item
                ), position
            )
        }
        ReadNotificationService.start(v.context, item.id)
    }

    override fun onItemLongClick(position: Int, v: View, item: GroupedNotificationModel?) {}
    override fun onError(throwable: Throwable) {
        onWorkOffline()
        super.onError(throwable)
    }

    override fun onWorkOffline() {
        if (notifications.isEmpty()) {
            val disposable = RxHelper.getObservable(
                Notification.getAllNotifications().toObservable()
            ).flatMap { notifications ->
                Observable.just(
                    construct(notifications.filterNotNull())
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
                val items = response.items
                manageDisposable(Notification.save(items))
                if (items != null && items.isNotEmpty()) {
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
            .filter { group: GroupedNotificationModel -> group.notification != null && group.notification!!.isUnread }
            .map(GroupedNotificationModel::notification)
            .subscribe({
                it?.let { notification ->
                    notification.isUnread = false
                    manageDisposable(notification.save(notification))
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
            .filter { group: GroupedNotificationModel -> group.notification != null && group.notification!!.isUnread }
            .filter { group: GroupedNotificationModel ->
                group.notification!!.repository!!.fullName.equals(
                    repo.fullName,
                    ignoreCase = true
                )
            }
            .map { it.notification!! }
            .subscribe({ notification: Notification ->
                notification.isUnread = false
                manageDisposable(notification.save(notification))
                sendToView { view ->
                    view?.onReadNotification(
                        notification
                    )
                }
            }) { throwable: Throwable -> onError(throwable) }
        manageDisposable(disposable)
    }
}