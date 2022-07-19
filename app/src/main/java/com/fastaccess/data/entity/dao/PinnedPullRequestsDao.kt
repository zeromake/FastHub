package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.PinnedPullRequests
import com.fastaccess.data.entity.PinnedPullRequests_
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class PinnedPullRequestsDao {
    companion object {
        val box: Box<PinnedPullRequests> by lazy { ObjectBox.boxStore.boxFor() }
        fun get(pullRequestId: Long): Single<Optional<PinnedPullRequests>> {
            return box.query()
                .equal(PinnedPullRequests_.pullRequestId, pullRequestId)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }

        fun delete(pullRequestId: Long): Single<Boolean> {
            return box.query()
                .equal(PinnedPullRequests_.pullRequestId, pullRequestId)
                .build()
                .toSingle {
                    it.remove() > 0
                }
        }

        fun updateEntry(pullRequestId: Long): Single<Optional<PinnedPullRequests>> {
            return get(pullRequestId).flatMap {
                if (!it.isEmpty()) {
                    val item = it.or()
                    item.entryCount++
                    box.put(item)
                }
                Single.just(it)
            }
        }

        fun isPinned(pullRequestId: Long): Single<Boolean> {
            return get(pullRequestId).flatMap {
                Single.just(!it.isEmpty())
            }
        }

        fun pinUpin(pullRequest: PullRequest): Single<Boolean> {
            return Single.zip(
                LoginDao.getUser(),
                get(pullRequest.id),
            ) { optional, prevPinned ->
                if (optional.isEmpty()) {
                    return@zip false
                }
                if (!prevPinned.isEmpty()) {
                    box.remove(prevPinned.or().id)
                    return@zip true
                }
                val pinned = PinnedPullRequests()
                pinned.login = optional.or().login
                pinned.pullRequest = pullRequest
                pinned.pullRequestId = pullRequest.id
                box.put(pinned)
                true
            }
        }

        fun getMyPinnedPullRequests(): Single<List<PullRequest>> {
            return LoginDao.getUser().flatMap { opt ->
                box.query()
                    .equal(PinnedPullRequests_.login, opt.or().login!!)
                    .orderDesc(PinnedPullRequests_.entryCount)
                    .orderDesc(PinnedPullRequests_.id)
                    .build()
                    .toSingle { query ->
                        query.find().map { it.pullRequest!! }
                    }
            }
        }
    }
}