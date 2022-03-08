package com.fastaccess.ui.modules.main.notifications

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.dao.model.AbstractFastHubNotification.NotificationType
import com.fastaccess.data.dao.model.FastHubNotification
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Kosh on 17.11.17.
 */
class FastHubNotificationDialog :
    BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    init {
        suppressAnimation = true
        isCancelable = false
    }

    private val model by lazy { arguments?.getParcelable<FastHubNotification>(BundleConstant.ITEM) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!
        root.findViewById<View>(R.id.cancel).setOnThrottleClickListener {
            dismiss()
        }
        return root
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val title = view.findViewById<FontTextView>(R.id.title)
        val description = view.findViewById<FontTextView>(R.id.description)
        model?.let {
            title?.text = it.title
            description?.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(it.body, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(it.body)
            }
            it.isRead = true
            FastHubNotification.update(it)
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

        fun show(fragmentManager: FragmentManager, model: FastHubNotification? = null) {
            val notification = model ?: FastHubNotification.getLatest()
            notification?.let {
                if (it.type == NotificationType.PROMOTION || it.type == NotificationType.PURCHASE && model == null) {
                    if (PrefGetter.isProEnabled) {
                        it.isRead = true
                        FastHubNotification.update(it)
                        return
                    }
                }
                newInstance(it).show(fragmentManager, TAG)
            }
        }

        fun show(fragmentManager: FragmentManager) {
            show(fragmentManager, null)
        }
    }
}