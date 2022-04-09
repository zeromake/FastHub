package com.fastaccess.data.entity.dao

import android.content.Context
import com.fastaccess.R
import com.fastaccess.data.dao.PullsIssuesParser.Companion.getForPullRequest
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.data.entity.PullRequest_
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.equal
import io.reactivex.Single

class PullRequestDao {
    companion object {
        val box: Box<PullRequest> by lazy { ObjectBox.boxStore.boxFor() }
        fun save(entity: PullRequest): Single<Long> {
            return box.toSingle {
                it.put(entity)
            }
        }

        fun save(models: List<PullRequest>, repoId: String, login: String): Single<Unit> {
            return box.query()
                .equal(PullRequest_.repoId, repoId)
                .equal(PullRequest_.login, login)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    models.forEach {
                        it.repoId = repoId
                        it.login = login
                    }
                    box.toSingle {
                        it.put(models)
                    }
                }
        }

        fun getPullRequests(
            repoId: String,
            login: String,
            issueState: IssueState
        ): Single<List<PullRequest>> {
            return box.query()
                .equal(PullRequest_.repoId, repoId)
                .equal(PullRequest_.login, login)
                .equal(PullRequest_.state, issueState.ordinal)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getPullRequestById(id: Long): Single<PullRequest> {
            return box.toSingle { it.get(id) }
        }

        fun getPullRequestByNumber(
            number: Int,
            repoId: String,
            login: String
        ): Single<Optional<PullRequest>> {
            return box.query()
                .equal(PullRequest_.number, number)
                .equal(PullRequest_.repoId, repoId)
                .equal(PullRequest_.login, login)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }

        fun getMergeBy(
            context: Context,
            pullRequest: PullRequest,
            showRepoName: Boolean
        ): SpannableBuilder {
            val isMerge = pullRequest.merged || !isEmpty(pullRequest.mergedAt)
            return if (isMerge) {
                val merger = pullRequest.mergedBy
                val builder = builder()
                if (showRepoName) {
                    val parser = getForPullRequest(pullRequest.htmlUrl!!)
                    if (parser != null) builder.bold(parser.login!!)
                        .append("/")
                        .bold(parser.repoId!!)
                        .append(" ")
                        .bold("#").bold(pullRequest.number.toString())
                        .append(" ")
                } else {
                    builder.bold("#" + pullRequest.number)
                        .append(" ")
                        .append(if (merger != null) merger.login + " " else "")
                }
                builder.append(context.getString(R.string.merged).lowercase())
                    .append(" ")
                if (pullRequest.head != null) {
                    builder.bold(pullRequest.head!!.ref!!)
                        .append(" ")
                        .append(context.getString(R.string.to))
                        .append(" ")
                        .bold(pullRequest.base!!.ref!!)
                        .append(" ")
                }
                builder.append(getTimeAgo(pullRequest.mergedAt))
                builder
            } else {
                val user = pullRequest.user
                val status = context.getString(pullRequest.state!!.status)
                val builder = builder()
                if (showRepoName) {
                    val parser = getForPullRequest(pullRequest.htmlUrl!!)
                    if (parser != null) {
                        builder.bold(parser.login!!)
                            .append("/")
                            .bold(parser.repoId!!)
                            .append(" ")
                            .bold("#").bold(pullRequest.number.toString())
                            .append(" ")
                    }
                } else {
                    builder.bold("#" + pullRequest.number)
                        .append(" ")
                        .append(user!!.login)
                        .append(" ")
                }
                if (pullRequest.state === IssueState.open && pullRequest.head != null && pullRequest.base != null) {
                    builder
                        .append(context.getString(R.string.want_to_merge))
                        .append(" ")
                        .bold(pullRequest.head!!.ref!!)
                        .append(" ")
                        .append(context.getString(R.string.to))
                        .append(" ")
                        .bold(pullRequest.base!!.ref!!)
                        .append(" ")
                        .append(getTimeAgo(if (pullRequest.state === IssueState.closed) pullRequest.closedAt else pullRequest.createdAt))
                } else {
                    builder
                        .bold(status.lowercase())
                        .append(" ")
                        .bold(if (pullRequest.head != null) pullRequest.head!!.ref!! else "")
                        .append(" ")
                        .append(getTimeAgo(if (pullRequest.state === IssueState.closed) pullRequest.closedAt else pullRequest.createdAt))
                }
            }
        }
    }
}