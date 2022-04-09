package com.fastaccess.ui.modules.repos.code.contributors

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener
import android.os.Bundle
import android.view.View
import com.fastaccess.data.entity.User
import com.fastaccess.ui.base.adapter.BaseViewHolder
import java.util.ArrayList

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface RepoContributorsMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onNotifyAdapter(items: List<User>?, page: Int)
        val loadMore: OnLoadMore<String>
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<User>,
        PaginationListener<String> {
        fun onFragmentCreated(bundle: Bundle)
        fun onWorkOffline()
        val users: ArrayList<User>
    }
}