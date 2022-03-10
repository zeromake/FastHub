package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.commits

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener
import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.model.Commit
import java.util.ArrayList

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface PullRequestCommitsMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onNotifyAdapter(items: List<Commit>, page: Int)
        val loadMore: OnLoadMore<*>
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<Commit>,
        PaginationListener<Any> {
        fun onFragmentCreated(bundle: Bundle)
        val commits: List<Commit>
        fun onWorkOffline()
    }
}