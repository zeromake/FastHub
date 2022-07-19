package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.PinnedRepos
import com.fastaccess.data.entity.PinnedRepos_
import com.fastaccess.data.entity.Repo
import com.fastaccess.utils.*
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Observable
import io.reactivex.Single

class PinnedReposDao {
    companion object {
        val box: Box<PinnedRepos> by lazy { ObjectBox.boxStore.boxFor() }

        fun update(entity: PinnedRepos): Single<Long> {
            return box.toSingle {
                it.put(entity)
            }
        }

        fun get(repoFullName: String): Single<Optional<PinnedRepos>> {
            return LoginDao.getUser().flatMap { optional ->
                val user = optional.get()
                user ?: return@flatMap Single.just(Optional.ofNullable(null))
                box.query()
                    .equal(PinnedRepos_.repoFullName, repoFullName)
                    .equal(PinnedRepos_.login, user.login!!)
                    .or()
                    .equal(PinnedRepos_.repoFullName, repoFullName)
                    .build()
                    .toSingleOptional {
                        it.findFirst()
                    }
            }
        }

        fun get(id: Long): Single<Optional<PinnedRepos>> {
            return box.toSingleOptional {
                it.get(id)
            }
        }

        fun isPinned(repoFullName: String): Single<Boolean> {
            return get(repoFullName).flatMap {
                Single.just(!it.isEmpty())
            }
        }

        fun pinUpin(repo: Repo): Single<Boolean> {
            return Single.zip(
                LoginDao.getUser(),
                get(repo.fullName!!),
            ) { optional, prevPinned ->
                if (optional.isEmpty()) {
                    return@zip false
                }
                if (!prevPinned.isEmpty()) {
                    box.remove(prevPinned.or().id)
                    return@zip false
                }
                val pinned = PinnedRepos()
                pinned.repoFullName = repo.fullName
                pinned.login = optional.or().login
                pinned.pinnedRepo = repo
                box.put(pinned)
                true
            }
        }

        fun updateEntry(repoFullName: String): Single<Optional<PinnedRepos>> {
            return box.query()
                .equal(PinnedRepos_.repoFullName, repoFullName)
                .build()
                .toSingleOptional { it.findFirst() }
                .flatMap { optional ->
                    val pinnedRepo = optional.get()
                    if (pinnedRepo != null) {
                        pinnedRepo.entryCount++
                        box.toSingle {
                            it.put(pinnedRepo)
                            optional
                        }
                    } else {
                        Single.just(optional)
                    }
                }
        }

        fun getMyPinnedRepos(): Single<List<PinnedRepos>> {
            return LoginDao.getUser().flatMap { userOptional ->
                val user = userOptional.or()
                box.query()
                    .equal(PinnedRepos_.login, user.login!!)
                    .or()
                    .isNull(PinnedRepos_.login)
                    .orderDesc(PinnedRepos_.entryCount)
                    .orderDesc(PinnedRepos_.id)
                    .build()
                    .toSingle {
                        it.find()
                    }
            }
        }

        fun getMenuRepos(): Observable<List<PinnedRepos>> {
            return LoginDao.getUser().toObservable().flatMap { userOptional ->
                val user = userOptional.or()
                box.query()
                    .equal(PinnedRepos_.login, user.login!!)
                    .orderDesc(PinnedRepos_.entryCount)
                    .orderDesc(PinnedRepos_.id)
                    .build()
                    .toObservable {
                        it.find(0, 5)
                    }
            }
        }

        fun delete(id: Long): Single<Boolean> {
            return box.toSingle { it.remove(id) }
        }
    }
}