package com.fastaccess.ui.modules.repos.extras.misc

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.model.User
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.UsersAdapter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.extras.misc.RepoMiscMvp.MiscType
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 04 May 2017, 8:41 PM
 */
class RepoMiscDialogFragment : BaseDialogFragment<RepoMiscMvp.View, RepoMiscPresenter>(),
    RepoMiscMvp.View {
    val toolbar: Toolbar? by viewFind(R.id.toolbar)
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)
    private var onLoadMore: OnLoadMore<Int>? = null
    private var adapter: UsersAdapter? = null
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
        return R.layout.milestone_dialog_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw NullPointerException("Bundle is null, username is required")
        }
        when (presenter.type) {
            RepoMiscMvp.FORKS -> {
                toolbar!!.setTitle(R.string.forks)
                stateLayout!!.setEmptyText(
                    String.format(
                        "%s %s",
                        getString(R.string.no),
                        getString(R.string.forks)
                    )
                )
            }
            RepoMiscMvp.STARS -> {
                toolbar!!.setTitle(R.string.stars)
                stateLayout!!.setEmptyText(
                    String.format(
                        "%s %s",
                        getString(R.string.no),
                        getString(R.string.stars)
                    )
                )
            }
            RepoMiscMvp.WATCHERS -> {
                toolbar!!.setTitle(R.string.watchers)
                stateLayout!!.setEmptyText(
                    String.format(
                        "%s %s",
                        getString(R.string.no),
                        getString(R.string.watchers)
                    )
                )
            }
        }
        toolbar!!.setNavigationIcon(R.drawable.ic_clear)
        toolbar!!.setNavigationOnClickListener { dismiss() }
        stateLayout!!.setOnReloadListener { presenter!!.onCallApi(1, null) }
        refresh!!.setOnRefreshListener { presenter!!.onCallApi(1, null) }
        recycler!!.setEmptyView(stateLayout!!, refresh)
        loadMore.initialize(presenter.currentPage, presenter.previousTotal)
        adapter = UsersAdapter(presenter.list)
        adapter!!.listener = presenter
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        recycler!!.addKeyLineDivider()
        if (presenter.list.isEmpty() && !presenter!!.isApiCalled) {
            presenter!!.onCallApi(1, null)
        }
        fastScroller!!.attachRecyclerView(recycler!!)
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

    override val loadMore: OnLoadMore<Int>
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter!!)
            }
            return onLoadMore!!
        }

    override fun providePresenter(): RepoMiscPresenter {
        return RepoMiscPresenter(arguments)
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    companion object {
        private fun newInstance(
            owner: String,
            repo: String,
            @MiscType type: Int
        ): RepoMiscDialogFragment {
            val view = RepoMiscDialogFragment()
            view.arguments = start()
                .put(BundleConstant.EXTRA, owner)
                .put(BundleConstant.ID, repo)
                .put(BundleConstant.EXTRA_TYPE, type)
                .end()
            return view
        }

        fun show(
            fragmentManager: FragmentManager, owner: String,
            repo: String, @MiscType type: Int
        ) {
            newInstance(owner, repo, type).show(
                fragmentManager,
                RepoMiscDialogFragment::class.java.name
            )
        }
    }
}