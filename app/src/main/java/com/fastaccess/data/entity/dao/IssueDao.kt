package com.fastaccess.data.entity.dao

import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Issue
import com.fastaccess.data.entity.Issue_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.equal
import io.reactivex.Single

class IssueDao {
    companion object {
        val box: Box<Issue> by lazy { ObjectBox.boxStore.boxFor()}

        fun save(entity: Issue): Single<Long> {
            return box.toSingle {
                it.put(entity)
            }
        }

        fun save(models: List<Issue>, repoId: String, login: String): Single<Unit> {
            return box.query()
                .equal(Issue_.repoId, repoId)
                .equal(Issue_.login, login)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    box.toSingle {
                        it.put(models)
                    }
                }
        }

        fun getIssues(repoId: String, login: String, issueState: IssueState): Single<List<Issue>> {
            return box.query()
                .equal(Issue_.repoId, repoId)
                .equal(Issue_.login, login)
                .equal(Issue_.state, issueState.ordinal)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getIssue(id: Long): Single<Issue> {
            return box.toSingle {
                it.get(id)
            }
        }

        fun getIssueByNumber(number: Int, repoId: String, login: String): Single<Optional<Issue>> {
            return box.query()
                .equal(Issue_.repoId, repoId)
                .equal(Issue_.login, login)
                .equal(Issue_.number, number)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }
    }
}