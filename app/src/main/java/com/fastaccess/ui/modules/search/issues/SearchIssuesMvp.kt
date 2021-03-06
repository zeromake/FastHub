package com.fastaccess.ui.modules.search.issues

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.entity.Issue
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.*

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface SearchIssuesMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onNotifyAdapter(items: List<Issue>?, page: Int)
        fun onSetTabCount(count: Int)
        fun onSetSearchQuery(query: String)
        fun onQueueSearch(query: String)
        val loadMore: OnLoadMore<String>
        fun onShowPopupDetails(item: Issue)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<Issue>,
        PaginationListener<String> {
        val issues: ArrayList<Issue>
    }
}