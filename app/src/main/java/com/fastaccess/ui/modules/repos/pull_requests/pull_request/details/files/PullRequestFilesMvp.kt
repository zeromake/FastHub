package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.CommitFileChanges
import com.fastaccess.data.dao.CommitFileModel
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.base.mvp.BaseMvp.*
import com.fastaccess.ui.modules.reviews.callback.ReviewCommentListener
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface PullRequestFilesMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener, OnToggleView,
        OnPatchClickListener, ReviewCommentListener {
        fun onNotifyAdapter(items: List<CommitFileChanges>, page: Int)
        val loadMore: OnLoadMore<*>
        fun onOpenForResult(position: Int, linesModel: CommitFileChanges)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<CommitFileChanges>,
        PaginationListener<Any?> {
        fun onFragmentCreated(bundle: Bundle)
        val files: ArrayList<CommitFileChanges>
        fun onWorkOffline()
    }

    interface OnPatchClickListener {
        fun onPatchClicked(
            groupPosition: Int,
            childPosition: Int,
            v: android.view.View?,
            commit: CommitFileModel,
            item: CommitLinesModel
        )
    }

    interface PatchCallback {
        fun onAddComment(comment: CommentRequestModel?)
    }

    interface CommitCommentCallback {
        val commitComment: ArrayList<CommentRequestModel?>
        fun onAddComment(comment: CommentRequestModel)
        fun hasReviewComments(): Boolean
    }
}