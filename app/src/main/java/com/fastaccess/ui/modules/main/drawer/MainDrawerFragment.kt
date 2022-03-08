package com.fastaccess.ui.modules.main.drawer

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import android.view.MenuItem
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.model.Login
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.about.FastHubAboutActivity
import com.fastaccess.ui.modules.gists.GistsListActivity
import com.fastaccess.ui.modules.main.MainActivity
import com.fastaccess.ui.modules.main.MainMvp
import com.fastaccess.ui.modules.main.donation.CheckPurchaseActivity
import com.fastaccess.ui.modules.main.playstore.PlayStoreWarningActivity
import com.fastaccess.ui.modules.notification.NotificationActivity
import com.fastaccess.ui.modules.pinned.PinnedReposActivity
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity
import com.fastaccess.ui.modules.trending.TrendingActivity
import com.fastaccess.ui.modules.user.UserPagerActivity

/**
 * Created by Kosh on 25.03.18.
 */
class MainDrawerFragment : BaseFragment<MainMvp.View, BasePresenter<MainMvp.View>>(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mainNav: NavigationView

    private val userModel by lazy { Login.getUser() }

    override fun fragmentLayout() = R.layout.main_nav_fragment_layout

    override fun providePresenter() = BasePresenter<MainMvp.View>()

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        mainNav = view.findViewById(R.id.mainNav)
        mainNav.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val activity = activity as? BaseActivity<*, *>? ?: return false
        activity.closeDrawer()
        if (item.isChecked) return false
        mainNav.postDelayed({
            if (!activity.isFinishing) {
                when (item.itemId) {
                    R.id.navToRepo -> activity.onNavToRepoClicked()
                    R.id.gists -> GistsListActivity.startActivity(activity)
                    R.id.pinnedMenu -> PinnedReposActivity.startActivity(activity)
                    R.id.mainView -> {
                        if (activity !is MainActivity) {
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            activity.startActivity(intent)
                            activity.finish()
                        }
                    }
                    R.id.profile -> userModel?.let {
                        UserPagerActivity.startActivity(activity, it.login, false, PrefGetter.isEnterprise, 0)
                    }
                    R.id.settings -> activity.onOpenSettings()
                    R.id.about -> activity.startActivity(Intent(activity, FastHubAboutActivity::class.java))
                    R.id.orgs -> activity.onOpenOrgsDialog()
                    R.id.notifications -> activity.startActivity(Intent(activity, NotificationActivity::class.java))
                    R.id.trending -> activity.startActivity(Intent(activity, TrendingActivity::class.java))
                    R.id.reportBug -> activity.startActivity(CreateIssueActivity.startForResult(activity))
                    R.id.faq -> activity.startActivity(Intent(activity, PlayStoreWarningActivity::class.java))
                    R.id.restorePurchase -> activity.startActivity(Intent(activity, CheckPurchaseActivity::class.java))
                }
            }
        }, 250)
        return true
    }

    fun getMenu() = mainNav.menu
}