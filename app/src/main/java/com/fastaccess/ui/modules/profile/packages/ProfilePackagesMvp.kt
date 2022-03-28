package com.fastaccess.ui.modules.profile.packages

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.dao.model.GitHubPackage
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.*

interface ProfilePackagesMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener {
        fun onNotifyAdapter(items: List<GitHubPackage>?, page: Int)
        val loadMore: OnLoadMore<String>
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<GitHubPackage>,
        PaginationListener<String> {
        val packages: ArrayList<GitHubPackage>
        fun onWorkOffline(login: String)
        var isOrg: Boolean
        var selectedType: String?
    }
}