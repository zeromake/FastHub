package com.fastaccess.ui.modules.repos.code.commit.details.files

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesMvp.OnPatchClickListener
import com.fastaccess.ui.modules.reviews.callback.ReviewCommentListener
import com.fastaccess.data.dao.CommitFileChanges
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import android.os.Bundle
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.entity.Comment
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */
interface CommitFilesMvp {
    interface View : FAView, OnToggleView, OnPatchClickListener, ReviewCommentListener {
        fun onNotifyAdapter(items: List<CommitFileChanges>?)
        fun onCommentAdded(newComment: Comment)
        fun clearAdapter()
        fun onOpenForResult(position: Int, model: CommitFileChanges?)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<CommitFileChanges> {
        fun onFragmentCreated(bundle: Bundle?)
        fun onSubmitComment(comment: String, item: CommitLinesModel, bundle: Bundle?)
        fun onSubmit(username: String?, name: String?, commentRequestModel: CommentRequestModel?)
    }
}