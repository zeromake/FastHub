package com.fastaccess.ui.modules.repos.code

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel.Companion.buildForRepoCode
import com.fastaccess.data.dao.TabsCountStateModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.ViewHelper.getTabTextView
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathFragment
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.fastaccess.ui.widgets.ViewPagerView
import com.google.android.material.tabs.TabLayout

/**
 * Created by Kosh on 31 Dec 2016, 1:36 AM
 */
class RepoCodePagerFragment : BaseFragment<RepoCodePagerMvp.View, RepoCodePagerPresenter>(),
    RepoCodePagerMvp.View {
    val tabs: TabLayout? by viewFind(R.id.tabs)
    val pager: ViewPagerView? by viewFind(R.id.pager)
    @State
    var counts = HashSet<TabsCountStateModel>()
    override fun fragmentLayout(): Int {
        return R.layout.tabbed_viewpager
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments != null) {
            val args = requireArguments()
            val repoId = args.getString(BundleConstant.ID)
            val login = args.getString(BundleConstant.EXTRA)
            val url = args.getString(BundleConstant.EXTRA_TWO)
            val htmlUrl = args.getString(BundleConstant.EXTRA_FOUR)
            val defaultBranch = args.getString(BundleConstant.EXTRA_THREE)
            if (isEmpty(repoId) || isEmpty(login) || isEmpty(url) || isEmpty(htmlUrl)) {
                return
            }
            pager!!.adapter = FragmentsPagerAdapter(
                childFragmentManager,
                buildForRepoCode(
                    requireContext(), repoId!!, login!!, url!!,
                    defaultBranch?:"master", htmlUrl!!
                )
            )
            tabs!!.tabMode = TabLayout.MODE_SCROLLABLE
            tabs!!.setupWithViewPager(pager)
        }
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

    override fun onScrollTop(index: Int) {
        if (pager == null || pager!!.adapter == null) return
        val fragment: Fragment = pager!!.adapter!!
            .instantiateItem(pager!!, index) as BaseFragment<*, *>
        if (fragment is BaseFragment<*, *>) {
            fragment.onScrollTop(index)
        }
    }

    override fun providePresenter(): RepoCodePagerPresenter {
        return RepoCodePagerPresenter()
    }

    override fun canPressBack(): Boolean {
        if (pager!!.currentItem != 1) return true
        val pathView = pager!!.adapter!!.instantiateItem(pager!!, 1) as RepoFilePathFragment
        return pathView.canPressBack()
    }

    override fun onBackPressed() {
        if (pager != null && pager!!.adapter != null) {
            val pathView = pager!!.adapter!!
                .instantiateItem(pager!!, 1) as RepoFilePathFragment
            pathView.onBackPressed()
        }
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

    private fun updateCount(model: TabsCountStateModel) {
        val tv = getTabTextView(tabs!!, model.tabIndex)
        tv.text = builder()
            .append(getString(R.string.commits))
            .append("   ")
            .append("(")
            .bold(java.lang.String.valueOf(model.count))
            .append(")")
    }

    companion object {
        val TAG: String = RepoCodePagerFragment::class.java.simpleName
        fun newInstance(
            repoId: String, login: String,
            htmlLink: String, url: String, defaultBranch: String
        ): RepoCodePagerFragment {
            val view = RepoCodePagerFragment()
            view.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, url)
                .put(BundleConstant.EXTRA_THREE, defaultBranch)
                .put(BundleConstant.EXTRA_FOUR, htmlLink)
                .end()
            return view
        }
    }
}