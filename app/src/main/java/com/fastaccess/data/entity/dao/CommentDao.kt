package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Comment
import com.fastaccess.data.entity.Comment_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class CommentDao {
    companion object {
        val box: Box<Comment> by lazy { ObjectBox.boxStore.boxFor() }

        fun saveForGist(models: List<Comment>, gistId: String): Single<Unit> {
            return box.query()
                .equal(Comment_.gistId, gistId)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    models.forEach {
                        it.gistId = gistId
                    }
                    box.toSingle {
                        it.put(models)
                    }
                }
        }

        fun saveForCommits(
            models: List<Comment>,
            repoId: String,
            login: String,
            commitId: String,
        ): Single<Unit> {
            return box.query()
                .equal(Comment_.repoId, repoId)
                .equal(Comment_.login, login)
                .equal(Comment_.commitId, commitId)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    models.forEach {
                        it.repoId = repoId
                        it.login = login
                        it.commitId = commitId
                    }
                    box.toSingle {
                        it.put(models)
                    }
                }
        }

        fun saveForIssues(
            models: List<Comment>,
            repoId: String,
            login: String,
            issueId: String
        ): Single<Unit> {
            return box.query()
                .equal(Comment_.repoId, repoId)
                .equal(Comment_.login, login)
                .equal(Comment_.issueId, issueId)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    models.forEach {
                        it.repoId = repoId
                        it.login = login
                        it.issueId = issueId
                    }
                    box.toSingle {
                        it.put(models)
                    }
                }
        }

        fun saveForPullRequests(
            models: List<Comment>,
            repoId: String,
            login: String,
            pullRequestId: String
        ): Single<Unit> {
            return box.query()
                .equal(Comment_.repoId, repoId)
                .equal(Comment_.login, login)
                .equal(Comment_.pullRequestId, pullRequestId)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    models.forEach {
                        it.repoId = repoId
                        it.login = login
                        it.pullRequestId = pullRequestId
                    }
                    box.toSingle {
                        it.put(models)
                    }
                }
        }

        fun getGistComments(gistId: String): Single<List<Comment>> {
            return box.query()
                .equal(Comment_.gistId, gistId)
                .orderDesc(Comment_.updatedAt)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getCommitComments(
            repoId: String,
            login: String,
            commitId: String
        ): Single<List<Comment>> {
            return box.query()
                .equal(Comment_.repoId, repoId)
                .equal(Comment_.login, login)
                .equal(Comment_.commitId, commitId)
                .orderDesc(Comment_.updatedAt)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getIssueComments(
            repoId: String,
            login: String,
            issueId: String
        ): Single<List<Comment>> {
            return box.query()
                .equal(Comment_.repoId, repoId)
                .equal(Comment_.login, login)
                .equal(Comment_.issueId, issueId)
                .orderDesc(Comment_.updatedAt)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getPullRequestComments(
            repoId: String,
            login: String,
            pullRequestId: String
        ): Single<List<Comment>> {
            return box.query()
                .equal(Comment_.repoId, repoId)
                .equal(Comment_.login, login)
                .equal(Comment_.pullRequestId, pullRequestId)
                .orderDesc(Comment_.updatedAt)
                .build()
                .toSingle {
                    it.find()
                }
        }
    }
}