package com.fastaccess.ui.modules.main.premium

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by kosh on 13/07/2017.
 */
class PremiumActivity : BaseActivity<PremiumMvp.View, BasePresenter<PremiumMvp.View>>(), PremiumMvp.View{
    private val cardsHolder: View by lazy { viewFind(R.id.cardsHolder)!! }

    override fun layout(): Int = R.layout.support_development_layout

    override val isTransparent: Boolean = true

    override fun providePresenter(): BasePresenter<PremiumMvp.View> = BasePresenter()

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = true

    fun onBuyAll() {
        PrefGetter.setProItems()
        PrefGetter.setEnterpriseItem()
        showMessage(getString(R.string.success), "\"Pro\" features unlocked, but don't forget to support development!")
        successResult()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnimHelper.animateVisibility(cardsHolder, true)
        listOf<View>(
            viewFind(R.id.two)!!,
            viewFind(R.id.five)!!
        ).setOnThrottleClickListener {
            when (it.id) {
                R.id.two -> onBuyAll()
                R.id.five -> startActivity(RepoPagerActivity.createIntent(this, "FastHub", "k0shk0sh"))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            successResult()
        }
    }

    private fun successResult() {
        hideProgress()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onSuccessfullyActivated() {}

    override fun onNoMatch() {
        hideProgress()
        showErrorMessage(getString(R.string.not_match))
    }

    private fun isGoogleSupported(): Boolean {
        if (AppHelper.isGoogleAvailable(this)) {
            return true
        }
        showErrorMessage(getString(R.string.google_play_service_error))
        return false
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PremiumActivity::class.java))
        }
    }
}