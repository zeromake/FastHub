package com.fastaccess.ui.modules.repos.code.commit.details

import android.content.Intent
import com.fastaccess.data.entity.Comment
import com.fastaccess.data.entity.Commit
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment.CommentListener

/**
 * Created by Kosh on 10 Dec 2016, 9:21 AM
 */
interface CommitPagerMvp {
    interface View : FAView, CommentListener {
        fun onSetup()
        fun onFinishActivity()
        fun onAddComment(newComment: Comment)
        val login: String?
        val repoId: String?
    }

    interface Presenter : FAPresenter {
        val commit: Commit?
        fun onActivityCreated(intent: Intent?)
        fun onWorkOffline(sha: String, repoId: String, login: String)
        val login: String?
        val repoId: String?
        fun showToRepoBtn(): Boolean
    }
}
