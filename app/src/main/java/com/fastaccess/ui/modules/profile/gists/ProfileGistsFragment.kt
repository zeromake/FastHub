package com.fastaccess.ui.modules.profile.gists

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.entity.Gist
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.GistsAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.gists.gist.GistActivity.Companion.createIntent
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */
class ProfileGistsFragment : BaseFragment<ProfileGistsMvp.View, ProfileGistsPresenter>(),
    ProfileGistsMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)
    private var adapter: GistsAdapter? = null
    private var onLoadMore: OnLoadMore<String>? = null
    override fun fragmentLayout(): Int {
        return R.layout.small_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (requireArguments().getString(BundleConstant.EXTRA) == null) {
            throw NullPointerException("Username is null")
        }
        stateLayout!!.setEmptyText(R.string.no_gists)
        refresh!!.setOnRefreshListener(this)
        stateLayout!!.setOnReloadListener(this)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        adapter = GistsAdapter(presenter!!.gists, true)
        adapter!!.listener = presenter
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        recycler!!.addDivider()
        if (presenter!!.gists.isEmpty() && !presenter!!.isApiCalled) {
            onRefresh()
        }
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun onRefresh() {
        presenter!!.onCallApi(1, requireArguments().getString(BundleConstant.EXTRA))
    }

    override fun onNotifyAdapter(items: List<Gist>?, page: Int) {
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

    override fun providePresenter(): ProfileGistsPresenter {
        return ProfileGistsPresenter()
    }

    override val loadMore: OnLoadMore<String>
        get() {
            if (onLoadMore == null) {
                onLoadMore =
                    OnLoadMore(presenter, requireArguments().getString(BundleConstant.EXTRA))
            }
            return onLoadMore!!
        }

    override fun onStartGistView(gistId: String) {
        launcher.launch(
            createIntent(requireContext(), gistId, isEnterprise),
        )
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val data = it.data
        if (it.resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                val gistsModel: Gist? = data.extras!!.getParcelable(BundleConstant.ITEM)
                if (gistsModel != null && adapter != null) {
                    adapter!!.removeItem(gistsModel)
                }
            } else {
                onRefresh()
            }
        }
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (recycler != null) recycler!!.scrollToPosition(0)
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    companion object {
        fun newInstance(login: String): ProfileGistsFragment {
            val view = ProfileGistsFragment()
            view.arguments = start()
                .put(BundleConstant.EXTRA, login)
                .put(
                    BundleConstant.IS_ENTERPRISE,
                    LoginDao.getUser().blockingGet().or().login.equals(login, ignoreCase = true)
                )
                .end()
            return view
        }
    }
}
