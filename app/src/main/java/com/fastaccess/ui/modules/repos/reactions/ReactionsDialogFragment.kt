package com.fastaccess.ui.modules.repos.reactions

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.data.entity.User
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.provider.timeline.CommentsHelper.getEmoji
import com.fastaccess.provider.timeline.ReactionsProvider.ReactionType
import com.fastaccess.ui.adapter.UsersAdapter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller
import com.google.android.material.appbar.AppBarLayout

/**
 * Created by Kosh on 11 Apr 2017, 11:30 AM
 */
class ReactionsDialogFragment :
    BaseDialogFragment<ReactionsDialogMvp.View, ReactionsDialogPresenter>(),
    ReactionsDialogMvp.View {
    val toolbar: Toolbar? by viewFind(R.id.toolbar)
    val appbar: AppBarLayout? by viewFind(R.id.appbar)
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)
    private var adapter: UsersAdapter? = null
    private var onLoadMore: OnLoadMore<String>? = null
    override fun fragmentLayout(): Int {
        return R.layout.milestone_dialog_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        toolbar!!.setNavigationIcon(R.drawable.ic_clear)
        toolbar!!.setNavigationOnClickListener { dismiss() }
        stateLayout!!.setEmptyText(R.string.no_reactions)
        stateLayout!!.setOnReloadListener { presenter!!.onCallApi(1, null) }
        refresh!!.setOnRefreshListener { presenter!!.onCallApi(1, null) }
        recycler!!.setEmptyView(stateLayout!!, refresh)
        adapter = UsersAdapter(presenter!!.users)
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        if (savedInstanceState == null) {
            presenter!!.onFragmentCreated(arguments)
        }
        toolbar!!.title = builder().append(getString(R.string.reactions))
            .append(" ")
            .append(getEmoji(presenter!!.reactionType!!))
        fastScroller!!.attachRecyclerView(recycler!!)
    }

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

    override fun providePresenter(): ReactionsDialogPresenter {
        return ReactionsDialogPresenter()
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            login: String, repoId: String,
            type: ReactionTypes, idOrNumber: Long,
            @ReactionType reactionType: Int
        ): ReactionsDialogFragment {
            val view = ReactionsDialogFragment()
            view.arguments = start()
                .put(BundleConstant.EXTRA_TYPE, type)
                .put(BundleConstant.EXTRA, repoId)
                .put(BundleConstant.EXTRA_TWO, login)
                .put(BundleConstant.EXTRA_THREE, reactionType)
                .put(BundleConstant.ID, idOrNumber)
                .end()
            return view
        }
    }
}