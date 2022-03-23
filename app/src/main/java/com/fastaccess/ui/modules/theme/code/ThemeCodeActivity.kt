package com.fastaccess.ui.modules.theme.code

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.SpinnerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.utils.setOnThrottleClickListener
import com.prettifier.pretty.PrettifyWebView
import com.prettifier.pretty.helper.CodeThemesHelper

/**
 * Created by Kosh on 21 Jun 2017, 2:01 PM
 */

class ThemeCodeActivity : BaseActivity<ThemeCodeMvp.View, ThemeCodePresenter>(), ThemeCodeMvp.View {
    val spinner: Spinner by lazy { viewFind(R.id.themesList)!! }
    val webView: PrettifyWebView by lazy { viewFind(R.id.webView)!! }
    val progress: ProgressBar by lazy { viewFind(R.id.readmeLoader)!! }

    override fun layout(): Int = R.layout.theme_code_layout

    override val isTransparent: Boolean = false

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): ThemeCodePresenter = ThemeCodePresenter()

    private fun onSaveTheme() {
        val theme = spinner.selectedItem as String
        PrefGetter.setCodeTheme(theme)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onInitAdapter(list: List<String>) {
        val adapter = SpinnerAdapter(this, list)
        spinner.adapter = adapter
        val themePosition = list.indexOf(PrefGetter.codeTheme)
        if (themePosition >= 0) spinner.setSelection(themePosition)
    }

    fun onItemSelect() {
        val theme = spinner.selectedItem as String
        progress.visibility = View.VISIBLE
        webView.setThemeSource(CodeThemesHelper.CODE_EXAMPLE, theme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewFind<View>(R.id.done)!!.setOnThrottleClickListener {
            onSaveTheme()
        }
        viewFind<Spinner>(R.id.themesList)!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    onItemSelect()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        progress.visibility = View.VISIBLE
        webView.setOnContentChangedListener(this)
        title = ""
        presenter.onLoadThemes()
    }

    override fun onContentChanged(progress: Int) {
        this.progress.let {
            it.progress = progress
            if (progress == 100) it.visibility = View.GONE
        }
    }

    override fun onScrollChanged(reachedTop: Boolean, scroll: Int) {}
}
