package com.fastaccess.ui.modules.repos.code.contributors

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.entity.User
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.*
import com.fastaccess.ui.modules.repos.code.contributors.graph.model.GraphStatModel

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface RepoContributorsMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onNotifyAdapter(items: List<User>?, page: Int)
        val loadMore: OnLoadMore<String>
        fun onShowGraph(user: User)
        var stats: GraphStatModel?
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<User>,
        PaginationListener<String> {
        fun onFragmentCreated(bundle: Bundle)
        fun onWorkOffline()
        val users: ArrayList<User>
        fun onShowPopupMenu(view: android.view.View, position: Int)
        fun retrieveStats(owner: String, repoID: String)
    }
}
