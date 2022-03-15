package com.fastaccess.provider.timeline

import androidx.annotation.IdRes
import androidx.annotation.IntDef
import com.fastaccess.data.dao.PostReactionModel
import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider
import io.reactivex.Observable
import retrofit2.Response
import kotlin.collections.LinkedHashMap
import kotlin.collections.MutableMap
import kotlin.collections.set

/**
 * Created by Kosh on 09 Apr 2017, 10:40 AM
 */
class ReactionsProvider {
    @IntDef(HEADER, COMMENT, REVIEW_COMMENT, COMMIT)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ReactionType

    private val reactionsMap: MutableMap<Long, ReactionsModel> = LinkedHashMap()
    fun onHandleReaction(
        @IdRes viewId: Int, idOrNumber: Long, login: String?,
        repoId: String?, @ReactionType reactionType: Int, isEnterprise: Boolean
    ): Observable<*>? {
        if (!isEmpty(login) && !isEmpty(repoId)) {
            if (!isPreviouslyReacted(idOrNumber, viewId)) {
                val reactionTypes = ReactionTypes[viewId]
                if (reactionTypes != null) {
                    var observable: Observable<ReactionsModel>? = null
                    when (reactionType) {
                        COMMENT -> observable = RestProvider.getReactionsService(isEnterprise)
                            .postIssueCommentReaction(
                                PostReactionModel(reactionTypes.content),
                                login!!,
                                repoId!!,
                                idOrNumber
                            )
                        HEADER -> observable = RestProvider.getReactionsService(isEnterprise)
                            .postIssueReaction(
                                PostReactionModel(reactionTypes.content),
                                login!!,
                                repoId!!,
                                idOrNumber
                            )
                        REVIEW_COMMENT -> observable =
                            RestProvider.getReactionsService(isEnterprise)
                                .postCommentReviewReaction(
                                    PostReactionModel(reactionTypes.content),
                                    login!!,
                                    repoId!!,
                                    idOrNumber
                                )
                        COMMIT -> observable = RestProvider.getReactionsService(isEnterprise)
                            .postCommitReaction(
                                PostReactionModel(reactionTypes.content),
                                login!!,
                                repoId!!,
                                idOrNumber
                            )
                    }
                    return if (observable == null) null else RxHelper.safeObservable(observable)
                        .doOnNext { response: ReactionsModel ->
                            reactionsMap[idOrNumber] = response
                        }
                }
            } else {
                val reactionsModel = reactionsMap[idOrNumber]
                if (reactionsModel != null) {
                    return RxHelper.safeObservable(
                        RestProvider.getReactionsService(isEnterprise).delete(reactionsModel.id)
                    )
                        .doOnNext { booleanResponse: Response<Boolean> ->
                            if (booleanResponse.code() == 204) {
                                reactionsMap.remove(idOrNumber)
                            }
                        }
                }
            }
        }
        return null
    }

    fun isPreviouslyReacted(idOrNumber: Long, @IdRes vId: Int): Boolean {
        val reactionsModel = reactionsMap[idOrNumber]
        if (reactionsModel == null || isEmpty(reactionsModel.content)) {
            return false
        }
        val type = ReactionTypes[vId]
        return type != null && type.equalsContent(reactionsModel.content)
    }

    fun isCallingApi(id: Long, vId: Int): Boolean {
        val reactionsModel = reactionsMap[id]
        if (reactionsModel == null || isEmpty(reactionsModel.content)) {
            return false
        }
        val type = ReactionTypes[vId]
        return type != null && type.equalsContent(reactionsModel.content) && reactionsModel.isCallingApi
    }

    companion object {
        const val HEADER = 0
        const val COMMENT = 1
        const val REVIEW_COMMENT = 2
        const val COMMIT = 3
    }
}