package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.RepoFile
import com.fastaccess.data.entity.RepoFile_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class RepoFileDao {
    companion object {
        val box: Box<RepoFile> by lazy { ObjectBox.boxStore.boxFor() }
        fun save(entity: RepoFile): Single<RepoFile> {
            return box.toSingle { it.put(entity) }.flatMap {
                Single.just(entity)
            }
        }

        fun save(models: List<RepoFile>, login: String, repoId: String): Single<Unit> {
            return box.query()
                .equal(RepoFile_.login, login)
                .equal(RepoFile_.repoId, repoId)
                .build()
                .toSingle {
                    it.remove()
                }.flatMap {
                    models.forEach {
                        it.login = login
                        it.repoId = repoId
                    }
                    box.toSingle { it.put(models) }
                }

        }

        fun getFiles(login: String, repoId: String): Single<List<RepoFile>> {
            return box.query()
                .equal(RepoFile_.login, login)
                .equal(RepoFile_.repoId, repoId)
                .order(RepoFile_.type)
                .build()
                .toSingle {
                    it.find()
                }
        }

        fun getFile(login: String, repoId: String, sha: String): Single<List<RepoFile>> {
            return box.query()
                .equal(RepoFile_.login, login)
                .equal(RepoFile_.repoId, repoId)
                .equal(RepoFile_.sha, sha)
                .order(RepoFile_.type)
                .build()
                .toSingle {
                    it.find()
                }
        }
    }
}