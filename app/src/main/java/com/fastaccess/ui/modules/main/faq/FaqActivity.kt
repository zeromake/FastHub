package com.fastaccess.ui.modules.main.faq

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter
import com.fastaccess.provider.timeline.HtmlHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Hashemsergani on 21.09.17.
 */
class FaqActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    fun onDone() {
        PrefGetter.setPlayStoreWarningShowed()
        finish()
    }

    override fun layout(): Int = R.layout.faq_view_layout

    override val isTransparent: Boolean = true

    override fun canBack(): Boolean = false

    override val isSecured: Boolean = true

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<View>(R.id.done).setOnThrottleClickListener {
            onDone()
        }
        val textView = findViewById<TextView>(R.id.description)
        textView.post {
            HtmlHelper.htmlIntoTextView(
                textView,
                getString(R.string.fasthub_faq_description),
                textView.width
            )
        }
    }
}