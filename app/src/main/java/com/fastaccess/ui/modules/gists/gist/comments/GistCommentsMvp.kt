package com.fastaccess.ui.modules.gists.gist.comments

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.model.User
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import java.util.ArrayList

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */
interface GistCommentsMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onNotifyAdapter(items: List<Comment>?, page: Int)
        fun onRemove(comment: Comment)
        val loadMore: OnLoadMore<String?>?
        fun onEditComment(item: Comment)
        fun onShowDeleteMsg(id: Long)
        fun onTagUser(user: User?)
        fun onReply(user: User?, message: String?)
        fun onHandleComment(text: String, bundle: Bundle?)
        fun onAddNewComment(comment: Comment)
        val namesToTag: ArrayList<String>
        fun hideBlockingProgress()
    }

    interface Presenter : FAPresenter, PaginationListener<String?>,
        BaseViewHolder.OnItemClickListener<Comment?> {
        val comments: ArrayList<Comment>
        fun onHandleDeletion(bundle: Bundle?)
        fun onWorkOffline(gistId: String)
        fun onHandleComment(text: String, bundle: Bundle?, gistId: String?)
    }
}