package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Notification
import com.fastaccess.data.entity.Notification_
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class NotificationDao {
    companion object {
        val box: Box<Notification> by lazy {  ObjectBox.boxStore.boxFor()}
        fun deleteAll(): Single<Unit> {
            return box.toSingle {
                it.removeAll()
            }
        }

        fun save(entity: Notification): Single<Long> {
            return box.toSingle {
                it.put(entity)
            }
        }

        fun save(models: List<Notification>): Single<Boolean> {
            if(models.isEmpty()) {
                return Single.just(false)
            }
            return box.toSingle {
                it.put(models)
                true
            }
        }
        fun saveAsSingle(models: List<Notification>): Single<Boolean> {
            return save(models)
        }

        fun getUnreadNotifications(): Single<List<Notification>> {
            return box.query()
                .equal(Notification_.unread, true)
                .orderDesc(Notification_.updatedAt)
                .build()
                .toSingle {
                    it.find()
                }
        }
        fun getAllNotifications(): Single<List<Notification>> {
            return box.query()
                .orderDesc(Notification_.updatedAt)
                .orderDesc(Notification_.unread)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun hasUnreadNotifications(): Single<Boolean> {
            return box.query()
                .equal(Notification_.unread, true)
                .build()
                .toSingle {
                    it.count() > 0
                }
        }

        fun markAsRead(id: Long): Single<Boolean> {
            return box.toSingleOptional {
                it.get(id)
            }.flatMap { optional ->
                if (optional.isEmpty()) {
                    return@flatMap Single.just(false)
                }
                val item = optional.or()
                item.unread = false
                box.toSingle {
                    it.put(item)
                    true
                }
            }
        }
    }
}