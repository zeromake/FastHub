package com.fastaccess.ui.modules.repos.projects.list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.github.RepoProjectsOpenQuery
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.ProjectsAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.RepoPagerMvp
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by kosh on 09/09/2017.
 */

class RepoProjectFragment : BaseFragment<RepoProjectMvp.View, RepoProjectPresenter>(),
    RepoProjectMvp.View {
    val recycler: DynamicRecyclerView by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout by viewFind(R.id.refresh)
    val stateLayout: StateLayout by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller by viewFind(R.id.fastScroller)
    private var onLoadMore: OnLoadMore<IssueState>? = null
    private val adapter by lazy { ProjectsAdapter(presenter.getProjects()) }
    private var badgeListener: RepoPagerMvp.TabsBadgeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is RepoPagerMvp.TabsBadgeListener) {
            badgeListener = parentFragment as RepoPagerMvp.TabsBadgeListener
        } else if (context is RepoPagerMvp.TabsBadgeListener) {
            badgeListener = context
        }
    }

    override fun onDetach() {
        badgeListener = null
        super.onDetach()
    }

    override fun providePresenter(): RepoProjectPresenter = RepoProjectPresenter()

    override fun onNotifyAdapter(items: List<RepoProjectsOpenQuery.Node>?, page: Int) {
        hideProgress()
        if (items == null || items.isEmpty()) {
            adapter.clear()
            return
        }
        if (page <= 1) {
            adapter.insertItems(items)
        } else {
            adapter.addItems(items)
        }
    }

    override fun onChangeTotalCount(count: Int) {
        badgeListener?.onSetBadge(if (getState() == IssueState.open) 0 else 1, count)
    }

    override fun getLoadMore(): OnLoadMore<IssueState> {
        if (onLoadMore == null) {
            onLoadMore = OnLoadMore(presenter)
        }
        onLoadMore!!.parameter = getState()
        return onLoadMore!!
    }

    override fun fragmentLayout(): Int = R.layout.micro_grid_refresh_list

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        stateLayout.setEmptyText(R.string.no_projects)
        stateLayout.setOnReloadListener { presenter.onCallApi(1, getState()) }
        refresh.setOnRefreshListener { presenter.onCallApi(1, getState()) }
        recycler.setEmptyView(stateLayout, refresh)
        getLoadMore().initialize(
            presenter.currentPage, presenter
                .previousTotal
        )
        adapter.listener = presenter
        recycler.adapter = adapter
        recycler.addDivider()
        recycler.addOnScrollListener(getLoadMore())
        fastScroller.attachRecyclerView(recycler)
        if (presenter.getProjects().isEmpty() && !presenter.isApiCalled) {
            presenter.onFragmentCreate(arguments)
            presenter.onCallApi(1, getState())
        }
    }

    override fun showProgress(@StringRes resId: Int) {
        refresh.isRefreshing = true
        stateLayout.showProgress()
    }

    override fun hideProgress() {
        refresh.isRefreshing = false
        stateLayout.hideProgress()
    }

    override fun showErrorMessage(msgRes: String) {
        showReload()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        recycler.scrollToPosition(0)
    }

    override fun onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore())
        super.onDestroyView()
    }

    private fun showReload() {
        hideProgress()
        stateLayout.showReload(adapter.itemCount)
    }

    private fun getState(): IssueState =
        requireArguments().getSerializable(BundleConstant.EXTRA_TYPE) as IssueState

    companion object {
        fun newInstance(
            login: String,
            repoId: String? = null,
            state: IssueState
        ): RepoProjectFragment {
            val fragment = RepoProjectFragment()
            fragment.arguments = Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TYPE, state)
                .end()
            return fragment
        }
    }
}