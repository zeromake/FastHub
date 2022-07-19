package com.fastaccess.ui.modules.user

import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel.Companion.buildForOrg
import com.fastaccess.data.dao.FragmentPagerAdapterModel.Companion.buildForProfile
import com.fastaccess.data.dao.TabsCountStateModel
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.helper.*
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.main.MainActivity
import com.fastaccess.ui.modules.profile.org.repos.OrgReposFragment
import com.fastaccess.ui.modules.profile.repos.ProfileReposFragment
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.ViewPagerView
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

/**
 * Created by Kosh on 03 Dec 2016, 8:00 AM
 */
class UserPagerActivity : BaseActivity<UserPagerMvp.View, UserPagerPresenter>(),
    UserPagerMvp.View {
    lateinit var tabs: TabLayout

    lateinit var pager: ViewPagerView

    lateinit var fab: FloatingActionButton

    @State
    var index = 0

    @State
    var login: String? = null

    @State
    var isOrg = false

    @State
    var counts = HashSet<TabsCountStateModel>()
    override fun layout(): Int {
        return R.layout.tabbed_pager_layout
    }

    override val isTransparent: Boolean = true

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): UserPagerPresenter {
        return UserPagerPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = window.decorView
        this.tabs = root.findViewById(R.id.tabs)
        this.pager = root.findViewById(R.id.tabbedPager)
        this.fab = root.findViewById(R.id.fab)
        this.fab.setOnThrottleClickListener {
            this.onRepoFilterClicked()
        }

        val currentUser = LoginDao.getUser().blockingGet().get()
        if (currentUser == null) {
            onRequireLogin()
            return
        }
        if (savedInstanceState == null) {
            if (intent != null && intent.extras != null) {
                login = intent.extras!!.getString(BundleConstant.EXTRA)
                isOrg = intent.extras!!.getBoolean(BundleConstant.EXTRA_TYPE)
                index = intent.extras!!.getInt(BundleConstant.EXTRA_TWO, -1)
                if (!InputHelper.isEmpty(login)) {
                    if (isOrg) {
                        presenter!!.checkOrgMembership(login!!)
                    } else {
                        if (!currentUser.login.equals(
                                login,
                                ignoreCase = true
                            )
                        ) presenter!!.onCheckBlocking(
                            login!!
                        )
                    }
                }
            } else {
                val user = LoginDao.getUser().blockingGet().get()
                if (user == null) {
                    onRequireLogin()
                    return
                }
                login = user.login
            }
        }
        if (InputHelper.isEmpty(login)) {
            finish()
            return
        }
        setTaskName(login)
        title = login
        if (login.equals(currentUser.login, ignoreCase = true)) {
            selectProfile()
        }
        if (!isOrg) {
            val adapter = FragmentsPagerAdapter(
                supportFragmentManager,
                buildForProfile(this, login!!)
            )
            pager.adapter = adapter
            tabs.tabGravity = TabLayout.GRAVITY_FILL
            tabs.tabMode = TabLayout.MODE_SCROLLABLE
            tabs.setupWithViewPager(pager)
            if (savedInstanceState == null) {
                if (index != -1) {
                    pager.currentItem = index
                }
            }
        } else {
            if (presenter!!.isMember == -1) {
                presenter!!.checkOrgMembership(login!!)
            } else {
                onInitOrg(presenter!!.isMember == 1)
            }
        }
        tabs.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(pager) {
            override fun onTabReselected(tab: TabLayout.Tab) {
                super.onTabReselected(tab)
                onScrollTop(tab.position)
            }
        })
        pager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                hideShowFab(position)
            }
        })
        if (!isOrg) {
            if (savedInstanceState != null && counts.isNotEmpty()) {
                counts.forEach { model: TabsCountStateModel -> updateCount(model) }
            }
        }
        hideShowFab(pager.currentItem)
    }

    override fun onScrollTop(index: Int) {
        if (pager.adapter == null) return
        val fragment = pager.adapter!!.instantiateItem(pager, index) as BaseFragment<*, *>
        fragment.onScrollTop(index)
    }

    override fun onNavigateToFollowers() {
        pager.currentItem = 7
    }

    override fun onNavigateToFollowing() {
        pager.currentItem = 8
    }

    override fun onInitOrg(isMember: Boolean) {
        hideProgress()
        val adapter = FragmentsPagerAdapter(
            supportFragmentManager,
            buildForOrg(this, login!!, isMember)
        )
        pager.adapter = adapter
        tabs.tabGravity = TabLayout.GRAVITY_FILL
        tabs.tabMode = TabLayout.MODE_SCROLLABLE
        tabs.setupWithViewPager(pager)
        setTaskName(login)
    }

    override fun onUserBlocked() {
        showMessage(R.string.success, R.string.user_blocked)
        onInvalidateMenu()
    }

    override fun onInvalidateMenu() {
        hideProgress()
        invalidateOptionsMenu()
    }

    override fun onUserUnBlocked() {
        showMessage(R.string.success, R.string.user_unblocked)
        onInvalidateMenu()
    }

    override fun onCheckType(isOrg: Boolean) {
        if (!this.isOrg == isOrg) {
            startActivity(this, login!!, isOrg, isEnterprise, index)
            finish()
        }
    }

    override fun onSetBadge(tabIndex: Int, count: Int) {
        val model = TabsCountStateModel()
        model.tabIndex = tabIndex
        model.count = count
        counts.add(model)
        updateCount(model)
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            MainActivity.launchMainActivity(this, true)
            finish()
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavBack() {
        if (isTaskRoot) {
            MainActivity.launchMainActivity(this, true)
        }
        finish()
    }

    private fun onRepoFilterClicked() {
        if (isOrg) {
            val position: Int = if (presenter!!.isMember == 1) {
                2
            } else {
                1
            }
            val fragment = pager.adapter!!
                .instantiateItem(pager, position) as OrgReposFragment
            fragment.onRepoFilterClicked()
        } else {
            val fragment = pager.adapter!!
                .instantiateItem(pager, 3) as ProfileReposFragment
            fragment.onRepoFilterClicked()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share && !InputHelper.isEmpty(login)) {
            ActivityHelper.shareUrl(
                this, Uri.Builder().scheme("https")
                    .authority(LinkParserHelper.HOST_DEFAULT)
                    .appendPath(login)
                    .toString()
            )
            return true
        } else if (item.itemId == R.id.block && !InputHelper.isEmpty(login)) {
            presenter!!.onBlockUser(login!!)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        Logger.e(presenter!!.isUserBlockedRequested, presenter!!.isUserBlocked)
        if (presenter!!.isUserBlockedRequested) {
            val login = LoginDao.getUser().blockingGet().get()
            if (login != null && !isOrg) {
                val username = login.login
                if (!username.equals(this.login, ignoreCase = true)) {
                    menu.findItem(R.id.block)
                        .setIcon(if (presenter!!.isUserBlocked) R.drawable.ic_unlock else R.drawable.ic_lock)
                        .setTitle(
                            if (presenter!!.isUserBlocked) getString(R.string.unblock) else getString(
                                R.string.block
                            )
                        ).isVisible = true
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun hideShowFab(position: Int) {
        if (isOrg) {
            if (presenter!!.isMember == 1) {
                if (position == 2) {
                    fab.show()
                } else {
                    fab.hide()
                }
            } else {
                if (position == 1) {
                    fab.show()
                } else {
                    fab.hide()
                }
            }
        } else {
            if (position == 3) {
                fab.show()
            } else {
                fab.hide()
            }
        }
    }

    private fun updateCount(model: TabsCountStateModel) {
        val tv = ViewHelper.getTabTextView(tabs, model.tabIndex)
        tv.text = SpannableBuilder.builder()
            .append(getString(R.string.starred))
            .append("   ")
            .append("(")
            .bold(java.lang.String.valueOf(model.count))
            .append(")")
    }

    companion object {
        @JvmStatic
        fun startActivity(
            context: Context, login: String, isOrg: Boolean,
            isEnterprise: Boolean, index: Int
        ) {
            context.startActivity(createIntent(context, login, isOrg, isEnterprise, index))
        }


        @JvmStatic
        fun createIntent(
            context: Context, login: String, isOrg: Boolean = false,
            isEnterprise: Boolean = false, index: Int = -1
        ): Intent {
            val intent = Intent(context, UserPagerActivity::class.java)
            intent.putExtras(
                Bundler.start()
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                    .put(BundleConstant.EXTRA_TYPE, isOrg)
                    .put(BundleConstant.EXTRA_TWO, index)
                    .end()
            )
            if (context is Service || context is Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return intent
        }
    }
}