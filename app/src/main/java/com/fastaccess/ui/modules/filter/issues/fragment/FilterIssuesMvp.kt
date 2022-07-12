package com.fastaccess.ui.modules.filter.issues.fragment

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.entity.Issue
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.*

/**
 * Created by Kosh on 09 Apr 2017, 7:06 PM
 */
interface FilterIssuesMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onClear()
        fun onSearch(query: String, isOpen: Boolean, isIssue: Boolean, isEnterprise: Boolean)
        fun onNotifyAdapter(items: List<Issue>?, page: Int)
        val loadMore: OnLoadMore<String>
        fun onSetCount(totalCount: Int)
        fun onItemClicked(item: Issue)
        fun onShowPopupDetails(item: Issue)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<Issue>,
        PaginationListener<String> {
        val issues: ArrayList<Issue>
    }
}