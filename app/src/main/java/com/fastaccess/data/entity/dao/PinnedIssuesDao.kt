package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Issue
import com.fastaccess.data.entity.PinnedIssues
import com.fastaccess.data.entity.PinnedIssues_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class PinnedIssuesDao {
    companion object {
        val box: Box<PinnedIssues> by lazy { ObjectBox.boxStore.boxFor() }
        fun get(issueId: Long): Single<Optional<PinnedIssues>> {
            return box.query()
                .equal(PinnedIssues_.issueId, issueId)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }

        fun delete(issueId: Long): Single<Long> {
            return box.query()
                .equal(PinnedIssues_.issueId, issueId)
                .build()
                .toSingle {
                    it.remove()
                }
        }

        fun updateEntry(issueId: Long): Single<Boolean> {
            return get(issueId).flatMap { optional ->
                if (optional.isEmpty()) {
                    return@flatMap Single.just(false)
                }
                val item = optional.or()
                item.entryCount++
                box.toSingle {
                    it.put(item)
                    true
                }
            }
        }

        fun getMyPinnedIssues(): Single<List<Issue>> {
            return LoginDao.getUser().flatMap { optional ->
                box.query()
                    .equal(PinnedIssues_.login, optional.or().login!!)
                    .orderDesc(PinnedIssues_.entryCount)
                    .orderDesc(PinnedIssues_.id)
                    .build()
                    .toSingle {
                        it.find().map { it.issue!! }
                    }
            }
        }

        fun isPinned(issueId: Long): Single<Boolean> {
            return get(issueId).flatMap {
                Single.just(!it.isEmpty())
            }
        }

        fun pinUpin(issue: Issue): Single<Boolean> {
            return Single.zip(
                LoginDao.getUser(),
                get(issue.id),
            ) { optional, prevPinned ->
                if (optional.isEmpty()) {
                    return@zip false
                }
                if (!prevPinned.isEmpty()) {
                    box.remove(prevPinned.or().id)
                    return@zip true
                }
                val pinned = PinnedIssues()
                pinned.login = optional.or().login
                pinned.issue = issue
                pinned.issueId = issue.id
                box.put(pinned)
                true
            }
        }
    }
}