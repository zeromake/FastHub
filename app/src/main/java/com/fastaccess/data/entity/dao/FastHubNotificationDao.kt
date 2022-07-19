package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.FastHubNotification
import com.fastaccess.data.entity.FastHubNotification_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.toObservable
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Observable
import io.reactivex.Single

class FastHubNotificationDao {
    companion object {
        val box: Box<FastHubNotification> by lazy { ObjectBox.boxStore.boxFor() }

        fun update(notification: FastHubNotification): Single<Long> {
            return box.toSingle {
                it.put(notification)
            }
        }

        fun save(notification: FastHubNotification): Single<Long> {
            return update(notification)
        }

        fun getLatest(): Single<Optional<FastHubNotification>> {
            return box.query()
                .equal(FastHubNotification_.read, false)
                .orderDesc(FastHubNotification_.date)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }

        fun getNotifications(): Observable<FastHubNotification> {
            return box.query()
                .orderDesc(FastHubNotification_.date)
                .build()
                .toObservable {
                    it.find()
                }.flatMap {
                    Observable.fromIterable(it)
                }
        }

        fun hasNotifications(): Single<Boolean> {
            return box.toSingle { it.count() > 0 }
        }
    }
}