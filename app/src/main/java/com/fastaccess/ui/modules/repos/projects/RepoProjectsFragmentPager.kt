package com.fastaccess.ui.modules.repos.projects

import android.os.Bundle
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.data.dao.TabsCountStateModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.ViewHelper
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.RepoPagerMvp
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.ViewPagerView
import com.google.android.material.tabs.TabLayout

/**
 * Created by kosh on 09/09/2017.
 */
class RepoProjectsFragmentPager : BaseFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(),
    RepoPagerMvp.TabsBadgeListener {

    val tabs: TabLayout by viewFind(R.id.tabs)
    val pager: ViewPagerView by viewFind(R.id.pager)
    private var counts: HashSet<TabsCountStateModel>? = null

    override fun fragmentLayout(): Int = R.layout.centered_tabbed_viewpager

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (counts?.isNotEmpty() == true) {
            outState.putSerializable("counts", counts)
        }
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val args = requireArguments()
        pager.adapter = FragmentsPagerAdapter(
            childFragmentManager, FragmentPagerAdapterModel.buildForRepoProjects(
                requireContext(),
                args.getString(BundleConstant.ID), args.getString(BundleConstant.EXTRA)!!
            )
        )
        tabs.setupWithViewPager(pager)
        if (savedInstanceState != null) {
            @Suppress("UNCHECKED_CAST")
            counts = savedInstanceState.getSerializable("counts") as? HashSet<TabsCountStateModel>?
            counts?.let { set ->
                if (set.isNotEmpty()) set.onEach { updateCount(it) }
            }
        } else {
            counts = hashSetOf()
        }
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun onSetBadge(tabIndex: Int, count: Int) {
        val model = TabsCountStateModel()
        model.tabIndex = tabIndex
        model.count = count
        counts?.add(model)
        updateCount(model)
    }

    private fun updateCount(model: TabsCountStateModel) {
        val tv = ViewHelper.getTabTextView(tabs, model.tabIndex)
        tv.text = SpannableBuilder.builder()
            .append(if (model.tabIndex == 0) getString(R.string.opened) else getString(R.string.closed))
            .append("   ")
            .append("(")
            .bold(model.count.toString())
            .append(")")
    }

    companion object {
        val TAG: String = RepoProjectsFragmentPager::class.java.simpleName
        fun newInstance(login: String, repoId: String? = null): RepoProjectsFragmentPager {
            val fragment = RepoProjectsFragmentPager()
            fragment.arguments = Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end()
            return fragment
        }
    }
}