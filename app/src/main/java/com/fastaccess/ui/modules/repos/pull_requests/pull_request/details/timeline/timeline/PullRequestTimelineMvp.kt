package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.dao.EditReviewCommentModel
import com.fastaccess.data.dao.ReviewCommentModel
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.model.PullRequest
import com.fastaccess.data.dao.model.User
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.provider.timeline.ReactionsProvider.ReactionType
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.adapter.callback.ReactionsCallback
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.*
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread

/**
 * Created by Kosh on 31 Mar 2017, 7:15 PM
 */
interface PullRequestTimelineMvp {
    interface ReviewCommentCallback {
        fun onClick(
            groupPosition: Int,
            commentPosition: Int,
            v: android.view.View,
            comment: ReviewCommentModel
        )

        fun onLongClick(
            groupPosition: Int,
            commentPosition: Int,
            v: android.view.View,
            comment: ReviewCommentModel
        )
    }

    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener, OnToggleView,
        ReactionsCallback {
        @CallOnMainThread
        fun onNotifyAdapter(items: List<TimelineModel>?, page: Int)
        val loadMore: OnLoadMore<PullRequest>
        fun onEditComment(item: Comment)
        fun onEditReviewComment(item: ReviewCommentModel, groupPosition: Int, childPosition: Int)
        fun onRemove(timelineModel: TimelineModel)
        fun onShowDeleteMsg(id: Long)
        fun onReply(user: User?, message: String?)
        fun showReactionsPopup(
            type: ReactionTypes,
            login: String,
            repoId: String,
            idOrNumber: Long,
            @ReactionType reactionType: Int
        )

        fun onShowReviewDeleteMsg(commentId: Long, groupPosition: Int, commentPosition: Int)
        fun onRemoveReviewComment(groupPosition: Int, commentPosition: Int)
        fun onSetHeader(timelineModel: TimelineModel)
        val pullRequest: PullRequest?
        fun onUpdateHeader()

        @CallOnMainThread
        fun showReload()
        fun onHandleComment(text: String, bundle: Bundle?)
        fun onReplyOrCreateReview(
            user: User?, message: String?, groupPosition: Int, childPosition: Int,
            model: EditReviewCommentModel
        )

        fun addComment(timelineModel: TimelineModel)
        val namesToTag: ArrayList<String>
        fun onHideBlockingProgress()
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<TimelineModel>,
        ReviewCommentCallback, PaginationListener<PullRequest> {
        val events: ArrayList<TimelineModel>
        fun onWorkOffline()
        fun onHandleDeletion(bundle: Bundle?)
        fun isPreviouslyReacted(commentId: Long, vId: Int): Boolean
        fun onHandleReaction(@IdRes vId: Int, idOrNumber: Long, @ReactionType reactionType: Int)
        fun isMerged(pullRequest: PullRequest?): Boolean
        fun isCallingApi(id: Long, vId: Int): Boolean
        fun onHandleComment(text: String, bundle: Bundle?)
    }
}