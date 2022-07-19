package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.Login
import com.fastaccess.data.entity.Login_
import com.fastaccess.helper.PrefGetter
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Flowable
import io.reactivex.Single

class LoginDao {
    companion object {
        val box: Box<Login> by lazy { ObjectBox.boxStore.boxFor() }

        //        val cache: Cache<String, Login> = Caffeine.newBuilder().build()
        fun save(entity: Login): Single<Login> {
            return box.query()
                .equal(Login_.login, entity.login!!)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }.flatMap { optional ->
                    if (!optional.isEmpty()) {
                        entity.id = optional.or().id
                    }
                    box.toSingle { it.put(entity) }.flatMap {
                        Single.just(entity)
                    }
                }
        }

        fun update(entity: Login): Single<Login> {
            return box.toSingle { it.put(entity) }.flatMap {
                Single.just(entity)
            }
        }

        fun getUser(): Single<Optional<Login>> {
            val query = box.query()
                .notNull(Login_.login)
                .equal(Login_.isLoggedIn, true)
                .build()
            return query.toSingleOptional { it.findFirst() }
        }

        fun getUser(login: String): Single<Optional<Login>> {
            return box.query()
                .equal(Login_.login, login)
                .notNull(Login_.token)
                .build()
                .toSingleOptional { it.findFirst() }
        }

        fun getAccounts(): Flowable<Login> {
            return RxQuery.flowableOneByOne(
                box
                    .query()
                    .equal(Login_.isLoggedIn, false)
                    .orderDesc(Login_.login)
                    .build()
            )
        }

        fun logout(): Single<Boolean> {
            // Todo PinnedRepos delete
            return getUser().flatMap { optional ->
                val user = optional.orElse(null)
                user ?: return@flatMap Single.just(false)
                box.toSingle { it.remove(user) }
            }
        }

        fun hasNormalLogin(): Single<Boolean> {
            return box
                .query()
                .isNull(Login_.isEnterprise)
                .or()
                .equal(Login_.isEnterprise, false)
                .build()
                .toSingle { it.count() > 0 }
        }

        fun onMultipleLogin(
            userModel: Login,
            isEnterprise: Boolean,
            isNew: Boolean
        ): Single<Long> {
            return getUser().flatMap { optional ->
                val currentUser = optional.orElse(null)
                if (currentUser != null) {
                    currentUser.isLoggedIn = false
                    box.put(currentUser)
                }
                if (!isEnterprise) {
                    PrefGetter.resetEnterprise()
                }
                userModel.isLoggedIn = true
                if (isNew) {
                    userModel.isEnterprise = isEnterprise
                    userModel.token =
                        if (isEnterprise) PrefGetter.enterpriseToken else PrefGetter.token
                    userModel.otpCode =
                        if (isEnterprise) PrefGetter.enterpriseOtpCode else PrefGetter.otpCode
                    userModel.enterpriseUrl =
                        if (isEnterprise) PrefGetter.enterpriseUrl else null
                } else {
                    if (isEnterprise) {
                        PrefGetter.setTokenEnterprise(userModel.token)
                        PrefGetter.enterpriseOtpCode = userModel.otpCode
                        PrefGetter.enterpriseUrl = userModel.enterpriseUrl
                    } else {
                        PrefGetter.resetEnterprise()
                        PrefGetter.token = userModel.token
                        PrefGetter.otpCode = userModel.otpCode
                    }
                }
                box.toSingle { it.put(userModel) }
            }
        }
    }
}