package com.fastaccess.ui.modules.repos.code.contributors

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.entity.User
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.UsersAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.code.contributors.graph.GraphContributorsFragment
import com.fastaccess.ui.modules.repos.code.contributors.graph.model.GraphStatModel
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller


/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */
class RepoContributorsFragment :
    BaseFragment<RepoContributorsMvp.View, RepoContributorsPresenter>(),
    RepoContributorsMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)
    private var onLoadMore: OnLoadMore<String>? = null
    private var adapter: UsersAdapter? = null
    override var stats: GraphStatModel? = null
    override fun onNotifyAdapter(items: List<User>?, page: Int) {
        hideProgress()
        if (items == null || items.isEmpty()) {
            adapter!!.clear()
            return
        }
        if (page <= 1) {
            adapter!!.insertItems(items)
        } else {
            adapter!!.addItems(items)
        }
    }

    override fun fragmentLayout(): Int {
        return R.layout.small_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw NullPointerException("Bundle is null, therefore, issues can't be proceeded.")
        }
        stateLayout!!.setEmptyText(R.string.no_contributors)
        stateLayout!!.setOnReloadListener(this)
        refresh!!.setOnRefreshListener(this)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        recycler!!.addKeyLineDivider()
        adapter = UsersAdapter(presenter!!.users, true)
        adapter!!.listener = presenter
        loadMore.initialize(presenter.currentPage, presenter.previousTotal)
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        if (savedInstanceState == null) {
            presenter!!.onFragmentCreated(requireArguments())
        } else if (presenter!!.users.isEmpty() && !presenter!!.isApiCalled) {
            onRefresh()
        }
        fastScroller!!.attachRecyclerView(recycler!!)
        presenter!!.retrieveStats(
            requireArguments().getString(BundleConstant.EXTRA)!!,
            requireArguments().getString(BundleConstant.ID)!!)
    }

    override fun providePresenter(): RepoContributorsPresenter {
        return RepoContributorsPresenter()
    }

    override fun showProgress(@StringRes resId: Int) {
        refresh!!.isRefreshing = true
        stateLayout!!.showProgress()
    }

    override fun hideProgress() {
        refresh!!.isRefreshing = false
        stateLayout!!.hideProgress()
    }

    override fun showErrorMessage(msgRes: String) {
        showReload()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    override val loadMore: OnLoadMore<String>
    get(){
        if (onLoadMore == null) {
            onLoadMore = OnLoadMore(presenter)
        }
        return onLoadMore!!
    }

    override fun onRefresh() {
        presenter!!.onCallApi(1, requireArguments().getString(BundleConstant.EXTRA))
        presenter!!.retrieveStats(
            requireArguments().getString(BundleConstant.EXTRA)!!,
            requireArguments().getString(BundleConstant.ID)!!)
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (recycler != null) recycler!!.scrollToPosition(0)
    }

    override fun onShowGraph(user: User) {
        val data: GraphStatModel.ContributionStats? = stats?.contributions?.find { it ->  it.author.login == user.login}
        if(data != null) {
            GraphContributorsFragment.newInstance(data)
                .show(childFragmentManager, "GraphContributorsFragment")
        } else {
            showMessage(R.string.error, R.string.network_error)
            onRefresh()
        }
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    companion object {
        fun newInstance(repoId: String, login: String): RepoContributorsFragment {
            val view = RepoContributorsFragment()
            view.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end()
            return view
        }
    }
}
