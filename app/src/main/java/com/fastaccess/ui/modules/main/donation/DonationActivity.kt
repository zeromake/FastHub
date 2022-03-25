package com.fastaccess.ui.modules.main.donation

import android.os.Bundle
import android.view.View
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper.animateVisibility
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.appbar.AppBarLayout

/**
 * Created by Kosh on 24 Mar 2017, 9:16 PM
 */
class DonationActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {
    val cardsHolder: View? by lazy { viewFind(R.id.cardsHolder) }
    val appBarLayout: AppBarLayout? by lazy { viewFind(R.id.appbar) }
    override fun layout(): Int {
        return R.layout.support_development_layout
    }

    override val isTransparent: Boolean
        get() = false

    override fun canBack(): Boolean {
        return true
    }

    override val isSecured: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = window.decorView
        listOf(
            R.id.two,
            R.id.five,
        ).map { root.findViewById<View>(it) }.setOnThrottleClickListener {
            when(it.id) {
                R.id.two -> {
                    PrefGetter.setProItems()
                    PrefGetter.setEnterpriseItem()
                    showMessage(getString(R.string.success), "\"Pro\" features unlocked, but don't forget to support development!")
                }
                R.id.five -> startActivity(RepoPagerActivity.createIntent(this, "FastHub", "k0shk0sh"))

            }
        }
        animateVisibility(cardsHolder, true)
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> {
        return BasePresenter()
    }
}