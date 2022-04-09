package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Gist
import com.fastaccess.data.entity.PinnedGists
import com.fastaccess.data.entity.PinnedGists_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class PinnedGistsDao {
    companion object {
        val box: Box<PinnedGists> by lazy { ObjectBox.boxStore.boxFor() }

        fun get(gistId: Long): Single<Optional<PinnedGists>> {
            return box.query()
                .equal(PinnedGists_.gistId, gistId)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }

        fun delete(gistId: Long): Single<Boolean> {
            return box.query()
                .equal(PinnedGists_.gistId, gistId)
                .build()
                .toSingle {
                    it.remove() > 0
                }
        }

        fun pinUpin(gist: Gist): Single<Boolean> {
            return Single.zip(
                get(gist.generateGistId),
                LoginDao.getUser(),
            ) { opt, loginOpt ->
                if (loginOpt.isEmpty()) {
                    return@zip false
                }
                if (!opt.isEmpty()) {
                    box.remove(opt.or().id)
                    return@zip true
                }
                val pinned = PinnedGists()
                pinned.login = loginOpt.or().login
                pinned.gist = gist
                pinned.gistId = gist.generateGistId
                box.put(pinned)
                true
            }
        }

        fun isPinned(gistId: Long): Single<Boolean> {
            return get(gistId).flatMap {
                return@flatMap Single.just(!it.isEmpty())
            }
        }

        fun updateEntry(gistId: Long): Single<Optional<PinnedGists>> {
            return get(gistId).flatMap {
                if (!it.isEmpty()) {
                    val item = it.or()
                    item.entryCount++
                    box.put(item)
                }
                Single.just(it)
            }
        }

        fun getMyPinnedGists(): Single<List<Gist>> {
            return LoginDao.getUser().flatMap { loginOpt ->
                box.query()
                    .equal(PinnedGists_.login, loginOpt.or().login!!)
                    .build()
                    .toSingle { query ->
                        query.find().map {
                            it.gist!!
                        }
                    }
            }
        }
    }
}