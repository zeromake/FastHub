package com.fastaccess.ui.modules.trending

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.databinding.TrendingActivityLayoutBinding
import com.fastaccess.helper.*
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.delegate.viewBinding
import com.fastaccess.ui.modules.main.MainActivity
import com.fastaccess.ui.modules.trending.fragment.TrendingFragment
import com.fastaccess.ui.widgets.FontEditText
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.navigation.NavigationView
import java.util.*


/**
 * Created by Kosh on 30 May 2017, 10:57 PM
 */

class TrendingActivity : BaseActivity<TrendingMvp.View, TrendingPresenter>(), TrendingMvp.View {
    private var trendingFragment: TrendingFragment? = null

//    private val binding: TrendingActivityLayoutBinding by viewBinding()
//    private val navMenu: NavigationView by lazy { binding.navMenu }
//    val daily: TextView by lazy { binding.daily }
//    val weekly: TextView by lazy { binding.weekly }
//    val monthly: TextView by lazy { binding.monthly }
//    val drawerLayout: DrawerLayout by lazy { binding.drawer }
//    val clear: View by lazy { binding.clear }
//    val searchEditText: FontEditText by lazy { binding.searchEditText }
    private val rootView: View by lazy { window.decorView }
    private val navMenu: NavigationView by lazy { rootView.findViewById(R.id.navMenu) }
    val daily: TextView by lazy { rootView.findViewById(R.id.daily) }
    val weekly: TextView by lazy { rootView.findViewById(R.id.weekly) }
    val monthly: TextView by lazy { rootView.findViewById(R.id.monthly) }
    val drawerLayout: DrawerLayout by lazy { rootView.findViewById(R.id.drawer) }
    val clear: View by lazy { rootView.findViewById(R.id.clear) }
    val searchEditText: FontEditText by lazy { rootView.findViewById(R.id.searchEditText) }


    @State
    var selectedTitle: String = TrendingModel.DEFAULT_LANG

    fun onTextChange(s: Editable) {
        val text = s.toString()
        if (text.isEmpty()) {
            AnimHelper.animateVisibility(clear, false)
        } else {
            AnimHelper.animateVisibility(clear, true)
        }
    }

    fun onSearch(): Boolean {
        presenter.onFilterLanguage(InputHelper.toString(searchEditText))
        ViewHelper.hideKeyboard(searchEditText)
        return true
    }

    private fun onDailyClicked() {
        daily.isSelected = true
        weekly.isSelected = false
        monthly.isSelected = false
        setValues()
    }

    private fun onWeeklyClicked() {
        weekly.isSelected = true
        daily.isSelected = false
        monthly.isSelected = false
        setValues()
    }

    private fun onMonthlyClicked() {
        monthly.isSelected = true
        weekly.isSelected = false
        daily.isSelected = false
        setValues()
    }

    private fun onClearSearch() {
        ViewHelper.hideKeyboard(searchEditText)
        searchEditText.setText("")
        onClearMenu()
        presenter.onLoadLanguage()
    }

    override fun layout(): Int = R.layout.trending_activity_layout

    override val isTransparent: Boolean = true

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): TrendingPresenter = TrendingPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        searchEditText.setOnEditorActionListener { _, _, _ ->
            onSearch()
        }

        searchEditText.addTextChangedListener({ _, _, _, _ -> }, { _, _, _, _ -> }) {
            onTextChange(it!!)
        }

        daily.setOnThrottleClickListener {
            onDailyClicked()
        }

        weekly.setOnThrottleClickListener {
            onWeeklyClicked()
        }

        monthly.setOnThrottleClickListener {
            onMonthlyClicked()
        }
        clear.setOnThrottleClickListener {
            onClearSearch()
        }
        navMenu.itemIconTintList = null
        trendingFragment =
            supportFragmentManager.findFragmentById(R.id.trendingFragment) as TrendingFragment?
        navMenu.setNavigationItemSelectedListener { item ->
            closeDrawerLayout()
            onItemClicked(item)
        }
        setupIntent(savedInstanceState)
        if (savedInstanceState == null) {
            presenter.onLoadLanguage()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                Logger.e(searchEditText.text)
                if (InputHelper.isEmpty(searchEditText)) { //searchEditText.text is always empty even tho there is a text in it !!!!!!!
                    presenter.onLoadLanguage()
                } else {
                    presenter.onFilterLanguage(InputHelper.toString(searchEditText))
                }
            }, 300)
        }
        onSelectTrending()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.trending_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("RtlHardcoded")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                drawerLayout.openDrawer(Gravity.RIGHT)
                true
            }
            R.id.share -> {
                val lang: String = when (selectedTitle) {
                    TrendingModel.DEFAULT_LANG -> ""
                    else -> selectedTitle
                }
                ActivityHelper.shareUrl(
                    this, "${LinkParserHelper.PROTOCOL_HTTPS}://${LinkParserHelper.HOST_DEFAULT}" +
                            "/trending/${
                                lang.replace(" ".toRegex(), "-").lowercase(Locale.getDefault())
                            }"
                )
                return true
            }
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAppend(title: String, color: Int) {
        navMenu.menu.add(R.id.languageGroup, title.hashCode(), Menu.NONE, title)
            .setCheckable(true)
            .setIcon(createOvalShape(color))
            .isChecked =
            title.lowercase(Locale.getDefault()) == selectedTitle.lowercase(Locale.getDefault())
    }

    override fun onClearMenu() {
        navMenu.menu.clear()
    }

    private fun onItemClicked(item: MenuItem?): Boolean {
        selectedTitle = item?.title.toString()
        Logger.e(selectedTitle)
        setValues()
        return true
    }

    @SuppressLint("RtlHardcoded")
    private fun closeDrawerLayout() {
        drawerLayout.closeDrawer(Gravity.RIGHT)
    }

    private fun setValues() {
        closeDrawerLayout()
        Logger.e(selectedTitle, getSince())
        trendingFragment?.onSetQuery(selectedTitle, getSince())
    }

    private fun getSince(): String {
        return when {
            daily.isSelected -> "daily"
            weekly.isSelected -> "weekly"
            monthly.isSelected -> "monthly"
            else -> "daily"
        }
    }

    private fun setupIntent(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            if (intent != null && intent.extras != null) {
                val bundle = intent.extras
                if (bundle != null) {
                    val lang: String = bundle.getString(BundleConstant.EXTRA)!!
                    val query: String? = bundle.getString(BundleConstant.EXTRA_TWO)
                    if (lang.isNotEmpty()) {
                        selectedTitle = lang
                    }
                    if (query.isNullOrEmpty()) {
                        daily.isSelected = true
                    } else {
                        when (query.lowercase(Locale.getDefault())) {
                            "daily" -> daily.isSelected = true
                            "weekly" -> weekly.isSelected = true
                            "monthly" -> monthly.isSelected = true
                        }
                    }
                } else {
                    daily.isSelected = true
                }
            } else {
                daily.isSelected = true
            }
            setValues()
        }
    }

    private fun createOvalShape(@ColorInt color: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setSize(24, 24)
        drawable.setColor(color)
        return drawable
    }

    companion object {
        fun getTrendingIntent(context: Context, lang: String?, query: String?): Intent {
            val intent = Intent(context, TrendingActivity::class.java)
            intent.putExtras(
                Bundler.start()
                    .put(BundleConstant.EXTRA, lang)
                    .put(BundleConstant.EXTRA_TWO, query)
                    .end()
            )
            return intent
        }
    }
}