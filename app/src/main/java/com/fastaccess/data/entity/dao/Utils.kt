package com.fastaccess.data.entity.dao

import com.fastaccess.utils.equal
import com.fastaccess.utils.notEqual
import io.objectbox.Box
import io.objectbox.Property
import io.reactivex.Single

class Utils {
    companion object {
        fun <T>saveRelation(
            box: Box<T>,
            models: List<T>,
            logins: String,
            property: Property<T>,
            forEachCall: (item: T, ids: MutableSet<Long>) -> Unit
        ): Single<Unit> {
            return LoginDao.getUser().flatMap { optional ->
                val login = optional.get()
                if (login != null) {
                    if (login.login!!.equals(logins, true)) {
                        val old = box.query()
                            .equal(property, logins)
                            .build()
                            .findIds().toMutableSet()
                        models.forEach {
                            forEachCall(it, old)
                        }
                        box.put(models)
                        if (old.isNotEmpty()) {
                            box.remove(*old.toLongArray())
                        }
                    } else {
                        box.query()
                            .notEqual(property, login.login!!)
                            .or()
                            .isNull(property)
                            .build()
                            .remove()
                    }
                }
                Single.just(Unit)
            }
        }
    }
}