package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Repo
import com.fastaccess.data.entity.Repo_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class RepoDao {
    companion object {
        val box: Box<Repo> by lazy { ObjectBox.boxStore.boxFor()}
        fun save(entity: Repo): Single<Long> {
            return box.toSingle { it.put(entity) }
        }

        fun getRepo(name: String, login: String): Single<Optional<Repo>> {
            return box.query()
                .equal(Repo_.fullName, "${login}/${name}")
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }


        fun getRepo(id: Long): Single<Optional<Repo>> {
            return box
                .toSingleOptional {
                    it.get(id)
                }
        }

        fun saveStarred(models: List<Repo>, starredUser: String): Single<Unit> {
            return Utils.saveRelation(box, models, starredUser, Repo_.starredUser) { item, ids ->
                item.starredUser = starredUser
                ids.remove(item.id)
            }
        }

        fun saveMyRepos(repos: List<Repo>, reposOwner: String): Single<Unit> {
            return Utils.saveRelation(box, repos, reposOwner, Repo_.reposOwner) { item, ids ->
                item.reposOwner = reposOwner
                ids.remove(item.id)
            }
        }

        fun getStarred(starredUser: String): Single<List<Repo>> {
            return box.query()
                .equal(Repo_.starredUser, starredUser)
                .orderDesc(Repo_.updatedAt)
                .build()
                .toSingle {
                    it.find()
                }
        }


        fun getMyRepos(reposOwner: String): Single<List<Repo>> {
            return box.query()
                .equal(Repo_.reposOwner, reposOwner)
                .orderDesc(Repo_.updatedAt)
                .build()
                .toSingle {
                    it.find()
                }
        }
    }
}