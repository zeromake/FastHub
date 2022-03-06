package com.prettifier.pretty.helper

import com.fastaccess.App
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.InputHelper
import java.io.IOException

/**
 * Created by Kosh on 21 Jun 2017, 1:44 PM
 */
object CodeThemesHelper {
    fun listThemes(): List<String> {
        try {
            val list = App.getInstance().assets.list("highlight/styles/themes")?.asSequence()
                ?.map { s: String -> "themes/$s" }
                ?.toMutableList()!!
            list.add(0, "prettify.css")
            list.add(1, "prettify_dark.css")
            return list
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun getTheme(isDark: Boolean): String {
        val theme = PrefGetter.getCodeTheme()
        return if (InputHelper.isEmpty(theme) || !exists(theme)) {
            if (!isDark) "prettify.css" else "prettify_dark.css"
        } else theme
    }

    private fun exists(theme: String): Boolean {
        return listThemes().contains(theme)
    }

    const val CODE_EXAMPLE =
        """class ThemeCodeActivity : BaseActivity<ThemeCodeMvp.View, ThemeCodePresenter>(), ThemeCodeMvp.View {

    val spinner: AppCompatSpinner by bindView(R.id.themesList)
    val webView: PrettifyWebView by bindView(R.id.webView)
    val progress: ProgressBar? by bindView(R.id.readmeLoader)

    override fun layout(): Int = R.layout.theme_code_layout

    override fun isTransparent(): Boolean = false

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): ThemeCodePresenter = ThemeCodePresenter()

    @OnClick(R.id.done) fun onSaveTheme() {
        val theme = spinner.selectedItem as String
        PrefGetter.setCodeTheme(theme)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onInitAdapter(list: List<String>) {
        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list)
    }

    @OnItemSelected(R.id.themesList) fun onItemSelect() {
        val theme = spinner.selectedItem as String
        webView.setThemeSource(CodeThemesHelper.CODE_EXAMPLE, theme)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress?.visibility = View.VISIBLE
        webView.setOnContentChangedListener(this)
        title = \"\"
        presenter.onLoadThemes()
    }

    override fun onContentChanged(p: Int) {
        progress?.let {
            it.progress = p
            if (p == 100) it.visibility = View.GONE
        }
    }

    override fun onScrollChanged(reachedTop: Boolean, scroll: Int) {}
}"""
}