package com.fastaccess.ui.modules.repos.code.commit

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.dao.BranchesModel
import com.fastaccess.data.dao.model.Commit
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.*
import com.fastaccess.ui.modules.repos.extras.branches.BranchesMvp.BranchSelectionListener

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface RepoCommitsMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener,
        BranchSelectionListener {
        fun onNotifyAdapter(items: List<Commit>?, page: Int)
        val loadMore: OnLoadMore<String>
        fun setBranchesData(branches: List<BranchesModel>?, firstTime: Boolean)
        fun onShowCommitCount(sum: Long)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<Commit>,
        PaginationListener<String> {
        fun onFragmentCreated(bundle: Bundle)
        val commits: ArrayList<Commit>
        fun onWorkOffline()
        fun onBranchChanged(branch: String)
        val defaultBranch: String?
    }
}