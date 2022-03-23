package com.fastaccess.ui.modules.main.premium

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.transition.TransitionManager
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.main.donation.DonateActivity
import com.fastaccess.utils.setOnThrottleClickListener
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Created by kosh on 13/07/2017.
 */
class PremiumActivity : BaseActivity<PremiumMvp.View, PremiumPresenter>(), PremiumMvp.View {
    val viewGroup: FrameLayout by lazy { viewFind(R.id.viewGroup)!! }
    private val progressLayout: View by lazy { viewFind(R.id.progressLayout)!! }
    val proPriceText: TextView by lazy { viewFind(R.id.proPrice)!! }
    val enterpriseText: TextView by lazy { viewFind(R.id.enterprisePrice)!! }
    private val buyAll: Button by lazy { viewFind(R.id.buyAll)!! }
    private var disposable: Disposable? = null
    private val allFeaturesKey by lazy { getString(R.string.fasthub_all_features_purchase) }
    private val enterpriseKey by lazy { getString(R.string.fasthub_enterprise_purchase) }
    private val proKey by lazy { getString(R.string.fasthub_pro_purchase) }

    override val isTransparent: Boolean = true

    override fun providePresenter(): BasePresenter<PremiumMvp.View> = BasePresenter()

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = true

    fun onBuyAll() {
        if (!isGoogleSupported()) return
        val price = buyAll.tag as? Long?
        DonateActivity.start(this, allFeaturesKey, price, buyAll.text.toString())
    }

    fun onBuyPro() {
        if (!isGoogleSupported()) return
        val price = proPriceText.tag as? Long?
        DonateActivity.start(this, proKey, price, proPriceText.text.toString())
    }

    fun onBuyEnterprise() {
        if (!isGoogleSupported()) return
        val price = enterpriseText.tag as? Long?
        DonateActivity.start(this, enterpriseKey, price, enterpriseText.text.toString())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listOf<View>(
            buyAll,
            viewFind(R.id.buyPro)!!,
            viewFind(R.id.buyEnterprise)!!,
            viewFind(R.id.close)!!,
        ).setOnThrottleClickListener {
            when (it.id) {
                R.id.buyAll -> onBuyAll()
                R.id.buyPro -> onBuyPro()
                R.id.buyEnterprise -> onBuyEnterprise()
                R.id.close -> finish()
            }
        }

        buyAll.text = getString(R.string.purchase_all).replace("%price%", "$7.99")
        val dis = RxHelper.getObservable(
            RxBillingService.getInstance(this, BuildConfig.DEBUG)
                .getSkuDetails(
                    ProductType.IN_APP,
                    arrayListOf(enterpriseKey, proKey, allFeaturesKey)
                )
                .toObservable()
        )
            .flatMap { Observable.fromIterable(it) }
            .subscribe({
                Logger.e(it.sku(), it.price(), it.priceCurrencyCode(), it.priceAmountMicros())
                when (it.sku()) {
                    enterpriseKey -> {
                        enterpriseText.text = it.price()
                        enterpriseText.tag = it.priceAmountMicros()
                    }
                    proKey -> {
                        proPriceText.text = it.price()
                        proPriceText.tag = it.priceAmountMicros()
                    }
                    allFeaturesKey -> {
                        buyAll.text =
                            getString(R.string.purchase_all).replace("%price%", it.price())
                        buyAll.tag = it.priceAmountMicros()
                    }
                }
            }, { t -> t.printStackTrace() })
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