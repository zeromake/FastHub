package com.fastaccess.ui.modules.repos.code.commit

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.BranchesModel
import com.fastaccess.data.entity.Commit
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.CommitsAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.RepoPagerMvp.TabsBadgeListener
import com.fastaccess.ui.modules.repos.extras.branches.pager.BranchesPagerFragment.Companion.newInstance
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */
class RepoCommitsFragment : BaseFragment<RepoCommitsMvp.View, RepoCommitsPresenter>(),
    RepoCommitsMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)

    val branches: FontTextView? by viewFind(R.id.branches)
    private var onLoadMore: OnLoadMore<String>? = null
    private var adapter: CommitsAdapter? = null
    private var tabsBadgeListener: TabsBadgeListener? = null

    private fun onBranchesClicked() {
        newInstance(presenter!!.login!!, presenter!!.repoId!!)
            .show(childFragmentManager, "BranchesFragment")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TabsBadgeListener) {
            tabsBadgeListener = context
        } else if (parentFragment is TabsBadgeListener) {
            tabsBadgeListener = parentFragment as TabsBadgeListener?
        }
    }

    override fun onDetach() {
        tabsBadgeListener = null
        super.onDetach()
    }

    override fun onNotifyAdapter(items: List<Commit>?, page: Int) {
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
        return R.layout.commit_with_branch_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw NullPointerException("Bundle is null, therefore, issues can't be proceeded.")
        }
        branches!!.setOnThrottleClickListener {
            onBranchesClicked()
        }
        stateLayout!!.setEmptyText(R.string.no_commits)
        stateLayout!!.setOnReloadListener(this)
        refresh!!.setOnRefreshListener(this)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        recycler!!.addKeyLineDivider()
        adapter = CommitsAdapter(presenter!!.commits)
        adapter!!.listener = presenter
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        if (savedInstanceState == null) {
            presenter!!.onFragmentCreated(requireArguments())
        } else if (presenter!!.commits.isEmpty() && !presenter!!.isApiCalled) {
            onRefresh()
        }
        branches!!.text = presenter!!.defaultBranch
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun providePresenter(): RepoCommitsPresenter {
        return RepoCommitsPresenter()
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
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter)
            }
            return onLoadMore!!
        }

    override fun setBranchesData(branches: List<BranchesModel>?, firstTime: Boolean) {}
    override fun onShowCommitCount(sum: Long) {
        if (tabsBadgeListener != null) {
            tabsBadgeListener!!.onSetBadge(2, sum.toInt())
        }
    }

    override fun onRefresh() {
        presenter!!.onCallApi(1, null)
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (recycler != null) recycler!!.scrollToPosition(0)
    }

    override fun onBranchSelected(branch: BranchesModel) {
        val ref = branch.name
        branches!!.text = ref
        presenter!!.onBranchChanged(ref!!)
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    companion object {
        @JvmOverloads
        fun newInstance(
            repoId: String, login: String, branch: String,
            path: String? = null
        ): RepoCommitsFragment {
            return newInstance(
                start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TWO, branch)
                    .put(BundleConstant.EXTRA_THREE, path)
                    .end()
            )
        }

        fun newInstance(bundle: Bundle): RepoCommitsFragment {
            val view = RepoCommitsFragment()
            view.arguments = bundle
            return view
        }
    }
}