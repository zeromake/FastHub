package com.fastaccess.ui.modules.repos.code.commit.details.comments

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.data.entity.Comment
import com.fastaccess.data.entity.User
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.adapter.callback.ReactionsCallback
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.*

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */
interface CommitCommentsMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener, OnToggleView,
        ReactionsCallback {
        fun onNotifyAdapter(items: List<TimelineModel>?, page: Int)
        fun onRemove(comment: TimelineModel)
        val loadMore: OnLoadMore<String>
        fun onEditComment(item: Comment)
        fun onShowDeleteMsg(id: Long)
        fun onTagUser(user: User?)
        fun onReply(user: User?, message: String?)
        fun showReactionsPopup(
            reactionTypes: ReactionTypes,
            login: String,
            repoId: String,
            commentId: Long
        )

        fun addComment(newComment: Comment)
        fun showReload()
        fun onHandleComment(text: String, bundle: Bundle?)
        val namesToTags: List<String>
        fun hideBlockingProgress()
    }

    interface Presenter : FAPresenter, PaginationListener<String>,
        BaseViewHolder.OnItemClickListener<TimelineModel> {
        fun onFragmentCreated(bundle: Bundle?)
        val comments: ArrayList<TimelineModel>
        fun onHandleDeletion(bundle: Bundle?)
        fun onWorkOffline()
        fun repoId(): String
        fun login(): String
        fun sha(): String?
        fun isPreviouslyReacted(commentId: Long, vId: Int): Boolean
        fun isCallingApi(id: Long, vId: Int): Boolean
        fun onHandleComment(text: String, bundle: Bundle?)
    }
}
