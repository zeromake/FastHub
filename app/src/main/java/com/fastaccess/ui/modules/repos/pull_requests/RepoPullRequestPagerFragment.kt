package com.fastaccess.ui.modules.repos.pull_requests

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import butterknife.BindView
import com.annimon.stream.Stream
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel.Companion.buildForRepoPullRequest
import com.fastaccess.data.dao.TabsCountStateModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.ViewHelper.getTabTextView
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.repos.RepoPagerMvp
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.fastaccess.ui.widgets.ViewPagerView
import com.google.android.material.tabs.TabLayout

/**
 * Created by Kosh on 31 Dec 2016, 1:36 AM
 */
class RepoPullRequestPagerFragment :
    BaseFragment<RepoPullRequestPagerMvp.View, RepoPullRequestPagerPresenter>(),
    RepoPullRequestPagerMvp.View {
    @JvmField
    @BindView(R.id.tabs)
    var tabs: TabLayout? = null

    @JvmField
    @BindView(R.id.pager)
    var pager: ViewPagerView? = null

    @JvmField
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
            buildForRepoPullRequest(requireContext(), login, repoId)
        )
        tabs!!.setupWithViewPager(pager)
        if (savedInstanceState != null && counts.isNotEmpty()) {
            Stream.of(counts).forEach { model: TabsCountStateModel -> updateCount(model) }
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

    override fun providePresenter(): RepoPullRequestPagerPresenter {
        return RepoPullRequestPagerPresenter()
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

    override val currentItem: Int
        get() = if (pager != null) pager!!.currentItem else 0

    override fun onScrolled(isUp: Boolean) {
        if (repoCallback != null) repoCallback!!.onScrolled(isUp)
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
        val TAG: String = RepoPullRequestPagerFragment::class.java.simpleName
        fun newInstance(repoId: String, login: String): RepoPullRequestPagerFragment {
            val view = RepoPullRequestPagerFragment()
            view.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end()
            return view
        }
    }
}