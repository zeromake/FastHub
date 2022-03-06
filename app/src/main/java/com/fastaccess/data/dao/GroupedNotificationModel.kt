package com.fastaccess.data.dao

import com.fastaccess.data.dao.model.Notification
import com.fastaccess.data.dao.model.Repo
import com.fastaccess.helper.InputHelper
import java.util.*

/**
 * Created by Kosh on 18 Apr 2017, 8:07 PM
 */
class GroupedNotificationModel {
    var type: Int
    var repo: Repo? = null
    var notification: Notification? = null
    var date: Date? = null

    private constructor(repo: Repo) {
        type = HEADER
        this.repo = repo
    }

    constructor(notification: Notification) {
        type = ROW
        this.notification = notification
        date = notification.updatedAt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val model = other as GroupedNotificationModel
        return notification != null && model.notification != null && notification!!.id == model.notification!!.id
    }

    override fun hashCode(): Int {
        return if (notification != null) InputHelper.getSafeIntId(notification!!.id) else 0
    }

    companion object {
        const val HEADER = 1
        const val ROW = 2

        @JvmStatic
        fun construct(items: List<Notification>?): List<GroupedNotificationModel> {
            val models: MutableList<GroupedNotificationModel> = ArrayList()
            if (items == null || items.isEmpty()) return models
            val grouped: Map<Repo, List<Notification>?> = items.asSequence()
                .filter { value: Notification -> !value.isUnread }
                .groupByTo(mutableMapOf()) { it.repository }

            grouped.asSequence()
                .filter { (_, value) -> value != null && value.isNotEmpty() }
                .forEach { (repo, notifications) ->
                    models.add(GroupedNotificationModel(repo))
                    notifications?.asSequence()?.sortedWith { o1: Notification, o2: Notification ->
                        o2.updatedAt.compareTo(o1.updatedAt)
                    }?.forEach { notification: Notification ->
                        models.add(
                            GroupedNotificationModel(
                                notification
                            )
                        )
                    }
                }
            return models
        }

        @JvmStatic
        fun onlyNotifications(items: List<Notification>?): List<GroupedNotificationModel> {
            return if (items == null || items.isEmpty()) ArrayList() else items.asSequence()
                .map { notification -> GroupedNotificationModel(notification) }
                .toList()
        }
    }
}