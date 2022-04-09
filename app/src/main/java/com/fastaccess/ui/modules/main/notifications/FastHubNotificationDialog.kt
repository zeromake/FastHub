package com.fastaccess.ui.modules.main.notifications

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.FragmentManager
import com.fastaccess.R
import com.fastaccess.data.entity.FastHubNotification
import com.fastaccess.data.entity.FastHubNotification.NotificationType
import com.fastaccess.data.entity.dao.FastHubNotificationDao
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.utils.Optional
import com.fastaccess.utils.setOnThrottleClickListener
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 17.11.17.
 */
class FastHubNotificationDialog :
    BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    init {
        suppressAnimation = true
        isCancelable = false
    }

    private val model by lazy {
        arguments?.getParcelable<FastHubNotification>(BundleConstant.ITEM)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.cancel).setOnThrottleClickListener {
            dismiss()
        }
        val title = view.findViewById<FontTextView>(R.id.title)
        val description = view.findViewById<FontTextView>(R.id.description)
        model?.let {
            title?.text = it.title
            description?.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(it.body, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(it.body)
            }
            it.read = true
            presenter.manageObservable(
                FastHubNotificationDao.update(it).toObservable()
            )
        } ?: dismiss()
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun fragmentLayout(): Int = R.layout.dialog_guide_layout

    companion object {
        @JvmStatic
        private val TAG = FastHubNotificationDialog::class.java.simpleName

        fun newInstance(model: FastHubNotification): FastHubNotificationDialog {
            val fragment = FastHubNotificationDialog()
            fragment.arguments = Bundler.start()
                .put(BundleConstant.ITEM, model)
                .end()
            return fragment
        }

        fun show(fragmentManager: FragmentManager, model: FastHubNotification? = null): Disposable {
            val notificationObservable = if (model != null)
                Observable.just(Optional.ofNullable(model)) else
                FastHubNotificationDao.getLatest().toObservable()
            val disposable = RxHelper.getObservable(
                notificationObservable
                    .flatMap {
                        var observable = Observable.just(0L)
                        if (!it.isEmpty()) {
                            val notification = it.or()
                            if (notification.type == NotificationType.PROMOTION || notification.type == NotificationType.PURCHASE && model == null) {
                                if (PrefGetter.isProEnabled) {
                                    notification.read = true
                                    observable =
                                        FastHubNotificationDao.update(notification).toObservable()
                                }
                            }
                            newInstance(notification).show(fragmentManager, TAG)
                        }
                        observable
                    }).subscribe({

            }) { obj: Throwable ->
                obj.printStackTrace()
            }
            return disposable
        }

        fun show(fragmentManager: FragmentManager): Disposable {
            return show(fragmentManager, null)
        }
    }
}