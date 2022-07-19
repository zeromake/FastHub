package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.ViewerFile
import com.fastaccess.data.entity.ViewerFile_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class ViewerFileDao {
    companion object {
        val box: Box<ViewerFile> by lazy { ObjectBox.boxStore.boxFor() }
        fun save(entity: ViewerFile): Single<ViewerFile> {
            return box.query()
                .equal(ViewerFile_.fullUrl, entity.fullUrl!!)
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

        fun get(url: String): Single<Optional<ViewerFile>> {
            return box.query().equal(
                ViewerFile_.fullUrl,
                url
            ).build().toSingleOptional { it.findFirst() }
        }
    }
}