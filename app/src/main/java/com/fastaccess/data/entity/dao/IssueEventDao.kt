package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.IssueEvent
import com.fastaccess.data.entity.IssueEvent_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class IssueEventDao {
    companion object {
        val box: Box<IssueEvent> by lazy { ObjectBox.boxStore.boxFor()}
        fun save(
            models: List<IssueEvent>,
            repoId: String,
            login: String,
            issueId: String
        ): Single<Unit> {
            return box.query()
                .equal(IssueEvent_.login, login)
                .equal(IssueEvent_.repoId, repoId)
                .equal(IssueEvent_.issueId, issueId)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    box.toSingle {
                        it.put(models)
                    }
                }
        }

        fun get(
            repoId: String,
            login: String,
            issueId: String,
        ): Single<List<IssueEvent>> {
            return box.query()
                .equal(IssueEvent_.login, login)
                .equal(IssueEvent_.repoId, repoId)
                .equal(IssueEvent_.issueId, issueId)
                .orderDesc(IssueEvent_.createdAt)
                .build()
                .toSingle {
                    it.find()
                }
        }
    }
}