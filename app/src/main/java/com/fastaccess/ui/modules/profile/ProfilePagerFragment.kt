package com.fastaccess.ui.modules.profile

import android.os.Bundle
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel.Companion.buildForProfile
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.widgets.ViewPagerView
import com.google.android.material.tabs.TabLayout

/**
 * Created by Kosh on 03 Dec 2016, 8:00 AM
 */
class ProfilePagerFragment : BaseFragment<ProfilePagerMvp.View, ProfilePagerPresenter>(),
    ProfilePagerMvp.View {
    val tabs: TabLayout? by viewFind(R.id.tabs)
    val pager: ViewPagerView? by viewFind(R.id.pager)
    override fun fragmentLayout(): Int {
        return R.layout.tabbed_viewpager
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw RuntimeException("Bundle is null?")
        }
        val login = requireArguments().getString(BundleConstant.EXTRA)
            ?: throw RuntimeException("user is null?")
        val adapter = FragmentsPagerAdapter(
            childFragmentManager,
            buildForProfile(requireContext(), login)
        )
        tabs!!.tabGravity = TabLayout.GRAVITY_FILL
        tabs!!.tabMode = TabLayout.MODE_SCROLLABLE
        pager!!.adapter = adapter
        tabs!!.setupWithViewPager(pager)
    }

    override fun providePresenter(): ProfilePagerPresenter {
        return ProfilePagerPresenter()
    }

    override fun onNavigateToFollowers() {
        pager!!.currentItem = 4
    }

    override fun onNavigateToFollowing() {
        pager!!.currentItem = 5
    }

    override fun onCheckType(isOrg: Boolean) {}

    companion object {
        val TAG: String = ProfilePagerFragment::class.java.simpleName
        fun newInstance(login: String): ProfilePagerFragment {
            val profileView = ProfilePagerFragment()
            profileView.arguments =
                start().put(BundleConstant.EXTRA, login).end()
            return profileView
        }
    }
}