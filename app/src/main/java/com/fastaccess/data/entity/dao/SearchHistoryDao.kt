package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.SearchHistory
import com.fastaccess.data.entity.SearchHistory_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Flowable
import io.reactivex.Single

class SearchHistoryDao {
    companion object {
        val box: Box<SearchHistory> by lazy { ObjectBox.boxStore.boxFor()}

        fun save(entity: SearchHistory): Single<SearchHistory> {
            return box.query()
                .equal(SearchHistory_.text, entity.text!!)
                .build()
                .toSingle { it.findIds(0, 1) }
                .flatMap { longs ->
                    if (longs.isNotEmpty()) {
                        entity.id = longs[0]
                    }
                    box.toSingle { it.put(entity) }
                }.flatMap {
                    if (entity.id != it) entity.id = it
                    Single.just(entity)
                }
        }

        fun getHistory(): Flowable<SearchHistory> {
            return RxQuery.flowableOneByOne(
                box.query()
                    .order(SearchHistory_.id)
                    .build()
            )
        }

        fun deleteAll() {
            box.removeAll()
        }
    }
}