package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Commit
import com.fastaccess.data.entity.Commit_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class CommitDao {
    companion object {
        val box: Box<Commit> by lazy { ObjectBox.boxStore.boxFor() }
        fun save(entity: Commit): Single<Long> {
            return box.toSingle {
                it.put(entity)
            }
        }

        fun save(models: List<Commit>, repoId: String, login: String): Single<Unit> {
            return box.query()
                .equal(Commit_.repoId, repoId)
                .equal(Commit_.login, login)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    models.forEach {
                        it.repoId = repoId
                        it.login = login
                    }
                    box.toSingle { it.put(models) }
                }
        }

        fun save(models: List<Commit>, repoId: String, login: String, number: Long): Single<Unit> {
            return box.query()
                .equal(Commit_.repoId, repoId)
                .equal(Commit_.login, login)
                .equal(Commit_.pullRequestNumber, number)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    models.forEach {
                        it.repoId = repoId
                        it.login = login
                        it.pullRequestNumber = number
                    }
                    box.toSingle { it.put(models) }
                }
        }

        fun getCommits(repoId: String, login: String): Single<List<Commit>> {
            return box.query()
                .equal(Commit_.repoId, repoId)
                .equal(Commit_.login, login)
                .equal(Commit_.pullRequestNumber, 0L)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getCommits(repoId: String, login: String, number: Long): Single<List<Commit>> {
            return box.query()
                .equal(Commit_.repoId, repoId)
                .equal(Commit_.login, login)
                .equal(Commit_.pullRequestNumber, number)
                .build()
                .toSingle {
                    it.find()
                }
        }


        fun getCommit(sha: String, repoId: String, login: String): Single<Optional<Commit>> {
            return box.query()
                .equal(Commit_.repoId, repoId)
                .equal(Commit_.login, login)
                .equal(Commit_.sha, sha)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }
    }

}