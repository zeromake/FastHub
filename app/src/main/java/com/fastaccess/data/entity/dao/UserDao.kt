package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.User
import com.fastaccess.data.entity.User_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Flowable
import io.reactivex.Single

class UserDao {
    companion object {
        val box: Box<User> by lazy { ObjectBox.boxStore.boxFor() }
        fun save(user: User): Single<Long> {
            return box.toSingle { it.put(user) }
        }

        fun getUser(login: String): Single<Optional<User>> {
            return box.query().equal(
                User_.login,
                login,
            ).build().toSingleOptional { it.findFirst() }
        }

        fun getUser(id: Long): Single<Optional<User>> {
            return box.query()
                .equal(User_.id, id)
                .build()
                .toSingleOptional { it.findFirst() }
        }

        fun saveUserFollowerList(users: List<User>, followingName: String): Single<Unit> {
            return Utils.saveRelation(box, users, followingName, User_.followingName) { item, ids ->
                item.followingName = followingName
                ids.remove(item.id)
            }
        }

        fun saveUserFollowingList(items: List<User>, followerName: String): Single<Unit> {
            return Utils.saveRelation(box, items, followerName, User_.followerName) { item, ids ->
                item.followerName = followerName
                ids.remove(item.id)
            }
        }

        fun getUserFollowerList(following: String): Flowable<User> {
            return RxQuery.flowableOneByOne(
                box
                    .query()
                    .equal(User_.followingName, following)
                    .build()
            )
        }

        fun getUserFollowingList(follower: String): Flowable<User> {
            return RxQuery.flowableOneByOne(
                box
                    .query()
                    .equal(User_.followerName, follower)
                    .build()
            )
        }
    }
}
