package com.fastaccess.ui.modules.profile.packages

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.model.GitHubPackage
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.PackagesAdapter
import com.fastaccess.ui.adapter.ReposAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.RepoPagerMvp.TabsBadgeListener
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

class ProfilePackagesFragment : BaseFragment<ProfilePackagesMvp.View, ProfilePackagesPresenter>(),
    ProfilePackagesMvp.View, AdapterView.OnItemSelectedListener {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)
    val packagesType: Spinner? by viewFind(R.id.packages_type)
    private var onLoadMore: OnLoadMore<String>? = null
    private var adapter: PackagesAdapter? = null
    private var tabsBadgeListener: TabsBadgeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is TabsBadgeListener) {
            tabsBadgeListener = parentFragment as TabsBadgeListener?
        } else if (context is TabsBadgeListener) {
            tabsBadgeListener = context
        }
    }

    override fun onDetach() {
        tabsBadgeListener = null
        super.onDetach()
    }

    override fun onNotifyAdapter(items: List<GitHubPackage>?, page: Int) {
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
        return R.layout.packages_with_chooser_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw NullPointerException("Bundle is null, username is required")
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.packages_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            packagesType!!.adapter = adapter
        }
        packagesType!!.onItemSelectedListener = this
        packagesType!!.setSelection(0)
        presenter!!.isOrg = requireArguments().getBoolean(BundleConstant.EXTRA_TWO)
        stateLayout!!.setEmptyText(R.string.no_packages)
        stateLayout!!.setOnReloadListener(this)
        refresh!!.setOnRefreshListener(this)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        adapter = PackagesAdapter(presenter!!.packages)
        adapter!!.listener = presenter
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        recycler!!.addDivider()
        if (presenter!!.packages.isEmpty() && !presenter!!.isApiCalled) {
            onRefresh()
        }
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun providePresenter(): ProfilePackagesPresenter {
        return ProfilePackagesPresenter()
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
                onLoadMore = OnLoadMore(presenter, requireArguments().getString(BundleConstant.EXTRA))
            }
            return onLoadMore!!
        }

    override fun onRefresh() {
        presenter!!.onCallApi(1, requireArguments().getString(BundleConstant.EXTRA))
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
        fun newInstance(username: String, isOrg: Boolean = false): ProfilePackagesFragment {
            val view = ProfilePackagesFragment()
            view.arguments = start()
                .put(BundleConstant.EXTRA, username)
                .put(BundleConstant.EXTRA_TWO, isOrg)
                .end()
            return view
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val selectedItem = packagesType!!.selectedItem.toString()
        if(!presenter!!.selectedType.equals(selectedItem)) {
            presenter!!.selectedType = selectedItem
            onRefresh()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}