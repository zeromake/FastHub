package com.fastaccess.ui.modules.repos.code.releases

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.SimpleUrlsModel
import com.fastaccess.data.dao.model.Release
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.provider.rest.RestProvider.downloadFile
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.ReleasesAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.dialog.ListDialogView
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.dialog.MessageDialogView.Companion.newInstance
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */
class RepoReleasesFragment : BaseFragment<RepoReleasesMvp.View, RepoReleasesPresenter>(),
    RepoReleasesMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)
    private var onLoadMore: OnLoadMore<String>? = null
    private var adapter: ReleasesAdapter? = null
    override fun onNotifyAdapter(items: List<Release>?, page: Int) {
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
        stateLayout!!.setEmptyText(R.string.no_releases)
        stateLayout!!.setOnReloadListener(this)
        refresh!!.setOnRefreshListener(this)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        recycler!!.addDivider()
        adapter = ReleasesAdapter(presenter!!.releases)
        adapter!!.listener = presenter
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        if (savedInstanceState == null) {
            presenter!!.onFragmentCreated(requireArguments())
        } else if (presenter!!.releases.isEmpty() && !presenter!!.isApiCalled) {
            onRefresh()
        }
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun providePresenter(): RepoReleasesPresenter {
        return RepoReleasesPresenter()
    }

    override val loadMore: OnLoadMore<String>
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter)
            }
            return onLoadMore!!
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

    override fun onDownload(item: Release) {
        val models = ArrayList<SimpleUrlsModel>()
        if (!isEmpty(item.zipBallUrl)) {
            val url = item.zipBallUrl
            models.add(SimpleUrlsModel(getString(R.string.download_as_zip), url))
        }
        if (!isEmpty(item.tarballUrl)) {
            val url = item.tarballUrl
            models.add(SimpleUrlsModel(getString(R.string.download_as_tar), url))
        }
        if (item.assets != null && !item.assets.isEmpty()) {
            val mapped = item.assets
                .filter { value -> value?.browserDownloadUrl != null }
                .map {
                    val assetsModel = it!!
                    SimpleUrlsModel(
                        "${assetsModel.name} (${assetsModel.downloadCount})",
                        assetsModel.browserDownloadUrl
                    )
                }
            if (mapped.isNotEmpty()) {
                models.addAll(mapped)
            }
        }
        val dialogView = ListDialogView<SimpleUrlsModel>()
        dialogView.initArguments(getString(R.string.releases), models)
        dialogView.show(childFragmentManager, "ListDialogView")
    }

    override fun onShowDetails(item: Release) {
        if (!isEmpty(item.body)) {
            newInstance(
                if (!isEmpty(item.name)) item.name else item.tagName,
                item.body, isMarkDown = true, hideCancel = false
            ).show(childFragmentManager, MessageDialogView.TAG)
        } else {
            showErrorMessage(getString(R.string.no_body))
        }
    }

    override fun onRefresh() {
        presenter!!.onCallApi(1, null)
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onItemSelected(item: SimpleUrlsModel) {
        downloadFile(requireContext(), item.url!!)
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
        fun newInstance(repoId: String, login: String): RepoReleasesFragment {
            val view = RepoReleasesFragment()
            view.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end()
            return view
        }

        @JvmStatic
        fun newInstance(
            repoId: String,
            login: String,
            tag: String?,
            id: Long
        ): RepoReleasesFragment {
            val view = RepoReleasesFragment()
            view.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, id)
                .put(BundleConstant.EXTRA_THREE, tag)
                .end()
            return view
        }
    }
}