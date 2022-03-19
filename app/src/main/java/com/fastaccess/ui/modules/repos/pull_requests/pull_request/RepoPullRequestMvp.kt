package com.fastaccess.ui.modules.repos.pull_requests.pull_request

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.data.dao.PullsIssuesParser
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener
import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.model.PullRequest
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.ui.base.adapter.BaseViewHolder
import java.util.ArrayList

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface RepoPullRequestMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onNotifyAdapter(items: List<PullRequest>?, page: Int)
        val loadMore: OnLoadMore<IssueState>
        fun onUpdateCount(totalCount: Int)
        fun onOpenPullRequest(parser: PullsIssuesParser)
        fun onShowPullRequestPopup(item: PullRequest)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<PullRequest>,
        PaginationListener<IssueState> {
        fun onFragmentCreated(bundle: Bundle)
        fun onWorkOffline()
        val pullRequests: ArrayList<PullRequest>
        val issueState: IssueState
    }

    companion object {
        const val PULL_REQUEST_REQUEST_CODE = 1003
    }
}