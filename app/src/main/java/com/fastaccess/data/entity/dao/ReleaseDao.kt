package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Release
import com.fastaccess.data.entity.Release_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single


class ReleaseDao {
    companion object {
        val box: Box<Release> by lazy { ObjectBox.boxStore.boxFor()}
        fun save(models: List<Release>, repoId: String, login: String): Single<Unit> {
            return box.query()
                .equal(Release_.login, login)
                .equal(Release_.repoId, repoId)
                .build()
                .toSingle {
                    it.remove()
                }
                .flatMap {
                    models.forEach { item ->
                        item.login = login
                        item.repoId = repoId
                    }
                    box.toSingle { it.put(models) }
                }
        }

        fun delete(repoId: String, login: String): Single<Long> {
            return box.query()
                .equal(Release_.login, login)
                .equal(Release_.repoId, repoId)
                .build()
                .toSingle {
                    it.remove()
                }
        }

        fun get(id: Long): Single<Release> {
            return box.toSingle { it.get(id) }
        }

        fun get(repoId: String, login: String): Single<List<Release>> {
            return box.query()
                .equal(Release_.login, login)
                .equal(Release_.repoId, repoId)
                .build()
                .toSingle { it.find() }
        }
    }
}