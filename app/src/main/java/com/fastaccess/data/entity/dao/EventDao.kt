package com.fastaccess.data.entity.dao

import android.text.TextUtils
import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Event
import com.fastaccess.data.entity.Event_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class EventDao {
    companion object {
        val box: Box<Event> by lazy { ObjectBox.boxStore.boxFor() }
        fun save(events: List<Event>, user: String): Single<Boolean> {
            return LoginDao.getUser().flatMap { loginOpt ->
                if (loginOpt.isEmpty()) {
                    return@flatMap Single.just(false)
                }
                val queryBuilder = box.query()
                queryBuilder.equal(Event_.login, user)
                queryBuilder
                    .build()
                    .remove()
                if (!TextUtils.equals(loginOpt.or().login, user)) {
                    return@flatMap Single.just(false)
                }
                events.forEach {
                    it.login = user
                }
                box.toSingle {
                    it.put(events)
                    true
                }
            }
        }

        fun getEvents(login: String): Single<List<Event>> {
            return box.query()
                .equal(Event_.login, login)
                .or()
                .isNull(Event_.login)
                .orderDesc(Event_.createdAt)
                .build()
                .toSingle {
                    it.find()
                }
        }
    }
}