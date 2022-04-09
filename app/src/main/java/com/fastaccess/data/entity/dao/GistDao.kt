package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Gist
import com.fastaccess.data.entity.Gist_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toObservable
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Observable
import io.reactivex.Single

class GistDao {
    companion object {
        val box: Box<Gist> by lazy { ObjectBox.boxStore.boxFor() }

        fun save(models: List<Gist>, ownerName: String): Single<Unit> {
            return Utils.saveRelation(
                box,
                models,
                ownerName,
                Gist_.ownerName,
            ) { item, ids ->
                ids.remove(item.id)
                item.ownerName = ownerName
            }
        }

        fun getMyGists(ownerName: String): Single<List<Gist>> {
            return box.query()
                .equal(Gist_.ownerName, ownerName)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getGists(): Single<List<Gist>> {
            return box.query()
                .isNull(Gist_.ownerName)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getGist(gistId: String): Observable<Gist> {
            return box.query()
                .equal(Gist_.gistId, gistId)
                .build()
                .toObservable {
                    it.find()
                }.flatMap {
                    Observable.fromIterable(it)
                }
        }
    }
}