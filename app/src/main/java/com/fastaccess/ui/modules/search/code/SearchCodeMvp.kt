package com.fastaccess.ui.modules.search.code

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.dao.SearchCodeModel
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.*

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface SearchCodeMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onNotifyAdapter(items: List<SearchCodeModel>?, page: Int)
        fun onSetTabCount(count: Int)
        fun onSetSearchQuery(query: String, showRepoName: Boolean)
        fun onQueueSearch(query: String)
        fun onQueueSearch(query: String, showRepoName: Boolean)
        val loadMore: OnLoadMore<String>
        fun onItemClicked(item: SearchCodeModel)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<SearchCodeModel>,
        PaginationListener<String> {
        val codes: ArrayList<SearchCodeModel>
    }
}