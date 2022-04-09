package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.NotificationQueue
import com.fastaccess.data.entity.NotificationQueue_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class NotificationQueueDao {
    companion object {
        val box: Box<NotificationQueue> by lazy { ObjectBox.boxStore.boxFor() }

        fun exists(notificationId: Long): Single<Boolean> {
            return box.query()
                .equal(NotificationQueue_.notificationId, notificationId)
                .build()
                .toSingle { it.findIds(0, 1).isNotEmpty() }
        }

        fun put(models: List<NotificationQueue>): Single<Unit> {
            return box.toSingle {
                it.removeAll()
            }.flatMap {
                box.toSingle {
                    it.put(models)
                }
            }
        }
    }
}