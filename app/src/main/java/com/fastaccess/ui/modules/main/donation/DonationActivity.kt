package com.fastaccess.ui.modules.main.donation

import android.os.Bundle
import android.view.View
import butterknife.BindView
import butterknife.OnClick
import com.fastaccess.App
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper.animateVisibility
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.main.donation.DonateActivity.Companion.enableProduct
import com.fastaccess.ui.modules.main.donation.DonateActivity.Companion.start
import com.fastaccess.ui.modules.main.premium.PremiumActivity.Companion.startActivity
import com.google.android.material.appbar.AppBarLayout
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.entities.ProductType

/**
 * Created by Kosh on 24 Mar 2017, 9:16 PM
 */
class DonationActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {
    @JvmField
    @BindView(R.id.cardsHolder)
    var cardsHolder: View? = null

    @JvmField
    @BindView(R.id.appbar)
    var appBarLayout: AppBarLayout? = null
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
        animateVisibility(cardsHolder, true)
        checkPurchase()
    }

    @OnClick(R.id.two)
    fun onTwoClicked() {
        onProceed(getString(R.string.donation_product_1))
    }

    @OnClick(R.id.five)
    fun onFiveClicked() {
        onProceed(getString(R.string.donation_product_2))
    }

    @OnClick(R.id.ten)
    fun onTenClicked() {
        onProceed(getString(R.string.donation_product_3))
    }

    @OnClick(R.id.twenty)
    fun onTwentyClicked() {
        onProceed(getString(R.string.donation_product_4))
    }

    @OnClick(R.id.premium)
    fun onNavToPremium() {
        startActivity(this)
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> {
        return BasePresenter()
    }

    private fun onProceed(productKey: String) {
        if (AppHelper.isGoogleAvailable(this)) {
            start(this, productKey, null, null)
        } else {
            showErrorMessage(getString(R.string.google_play_service_error))
        }
    }

    private fun checkPurchase() {
        (presenter as BasePresenter<*>?)!!.manageViewDisposable(RxBillingService.getInstance(
            this,
            BuildConfig.DEBUG
        )
            .getPurchases(ProductType.IN_APP)
            .subscribe { purchases, _ ->
                if (purchases != null && purchases.isNotEmpty()) {
                    for (purchase in purchases) {
                        val sku = purchase.sku()
                        if (!isEmpty(sku)) {
                            enableProduct(sku, App.getInstance())
                        }
                    }
                }
            })
    }
}