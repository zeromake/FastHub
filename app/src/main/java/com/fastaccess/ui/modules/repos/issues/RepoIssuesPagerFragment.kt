package com.fastaccess.ui.modules.repos.issues

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel.Companion.buildForRepoIssue
import com.fastaccess.data.dao.TabsCountStateModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.ViewHelper.getTabTextView
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.RepoPagerMvp
import com.fastaccess.ui.modules.repos.issues.issue.RepoClosedIssuesFragment
import com.fastaccess.ui.modules.repos.issues.issue.RepoOpenedIssuesFragment
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.fastaccess.ui.widgets.ViewPagerView
import com.google.android.material.tabs.TabLayout

/**
 * Created by Kosh on 31 Dec 2016, 1:36 AM
 */
class RepoIssuesPagerFragment : BaseFragment<RepoIssuesPagerMvp.View, RepoIssuesPagerPresenter>(),
    RepoIssuesPagerMvp.View {
    val tabs: TabLayout? by viewFind(R.id.tabs)
    val pager: ViewPagerView? by viewFind(R.id.pager)

    @State
    var counts = HashSet<TabsCountStateModel>()
    private var repoCallback: RepoPagerMvp.View? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is RepoPagerMvp.View) {
            repoCallback = parentFragment as RepoPagerMvp.View?
        } else if (context is RepoPagerMvp.View) {
            repoCallback = context
        }
    }

    override fun onDetach() {
        repoCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int {
        return R.layout.centered_tabbed_viewpager
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val repoId = requireArguments().getString(BundleConstant.ID)
        val login = requireArguments().getString(BundleConstant.EXTRA)
        if (login == null || repoId == null) throw NullPointerException("repoId || login is null???")
        pager!!.adapter = FragmentsPagerAdapter(
            childFragmentManager,
            buildForRepoIssue(requireContext(), login, repoId)
        )
        tabs!!.setupWithViewPager(pager)
        if (savedInstanceState != null && counts.isNotEmpty()) {
            counts.forEach { model: TabsCountStateModel -> updateCount(model) }
        }
        tabs!!.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(pager) {
            override fun onTabReselected(tab: TabLayout.Tab) {
                super.onTabReselected(tab)
                onScrollTop(tab.position)
            }
        })
    }

    override fun providePresenter(): RepoIssuesPagerPresenter {
        return RepoIssuesPagerPresenter()
    }

    override fun onAddIssue() {
        if (pager!!.currentItem != 0) pager!!.currentItem = 0
        val repoOpenedIssuesView = pager!!.adapter!!
            .instantiateItem(pager!!, 0) as RepoOpenedIssuesFragment
        repoOpenedIssuesView.onAddIssue()
    }

    override fun setCurrentItem(index: Int, refresh: Boolean) {
        if (pager == null || pager!!.adapter == null) return
        if (!refresh) pager!!.setCurrentItem(index, true)
        if (index == 1 && refresh) {
            val closedIssues = pager!!.adapter!!
                .instantiateItem(pager!!, 1) as RepoClosedIssuesFragment
            closedIssues.onRefresh()
        } else if (index == 0 && refresh) {
            val openedIssues = pager!!.adapter!!
                .instantiateItem(pager!!, 0) as RepoOpenedIssuesFragment
            openedIssues.onRefresh()
        }
    }

    override val currentItem: Int
        get() = if (pager != null) pager!!.currentItem else 0

    override fun onScrolled(isUp: Boolean) {
        if (repoCallback != null) repoCallback!!.onScrolled(isUp)
    }

    override fun onSetBadge(tabIndex: Int, count: Int) {
        val model = TabsCountStateModel()
        model.tabIndex = tabIndex
        model.count = count
        counts.add(model)
        if (tabs != null) {
            updateCount(model)
        }
    }

    override fun onChangeIssueSort(isLastUpdated: Boolean) {
        if (pager == null || pager!!.adapter == null) return
        val closedIssues = pager!!.adapter!!
            .instantiateItem(pager!!, 1) as RepoClosedIssuesFragment
        closedIssues.onRefresh(isLastUpdated)
        val openedIssues = pager!!.adapter!!
            .instantiateItem(pager!!, 0) as RepoOpenedIssuesFragment
        openedIssues.onRefresh(isLastUpdated)
    }

    override fun onScrollTop(index: Int) {
        if (pager == null || pager!!.adapter == null) return
        val fragment: Fragment = pager!!.adapter!!
            .instantiateItem(pager!!, index) as BaseFragment<*, *>
        if (fragment is BaseFragment<*, *>) {
            fragment.onScrollTop(index)
        }
    }

    private fun updateCount(model: TabsCountStateModel) {
        val tv = getTabTextView(tabs!!, model.tabIndex)
        tv.text = builder()
            .append(if (model.tabIndex == 0) getString(R.string.opened) else getString(R.string.closed))
            .append("   ")
            .append("(")
            .bold(java.lang.String.valueOf(model.count))
            .append(")")
    }

    companion object {
        val TAG: String = RepoIssuesPagerFragment::class.java.simpleName
        fun newInstance(repoId: String, login: String): RepoIssuesPagerFragment {
            val view = RepoIssuesPagerFragment()
            view.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end()
            return view
        }
    }
}