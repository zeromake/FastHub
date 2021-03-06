package com.fastaccess.ui.modules.theme.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.main.premium.PremiumActivity
import com.fastaccess.ui.widgets.SpannableBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by Kosh on 08 Jun 2017, 10:53 PM
 */

class ThemeFragment : BaseFragment<ThemeFragmentMvp.View, ThemeFragmentPresenter>(),
    ThemeFragmentMvp.View {

    lateinit var apply: FloatingActionButton
    lateinit var toolbar: Toolbar

    private val mTheme = "appTheme"
    private var primaryDarkColor: Int = 0
    private var theme: Int = 0
    private var themeListener: ThemeFragmentMvp.ThemeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        themeListener = context as ThemeFragmentMvp.ThemeListener
    }

    override fun onDetach() {
        themeListener = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = 0

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        apply = view.findViewById(R.id.apply)
        toolbar = view.findViewById(R.id.toolbar)
        apply.setOnClickListener {
            setTheme()
        }
        if (isPremiumTheme()) {
            toolbar.title =
                SpannableBuilder.builder().foreground(getString(R.string.premium_theme), Color.RED)
        }
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        theme = requireArguments().getInt(BundleConstant.ITEM)
        val contextThemeWrapper = ContextThemeWrapper(activity, theme)
        primaryDarkColor = ViewHelper.getPrimaryDarkColor(contextThemeWrapper)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.theme_layout, container, false)!!
    }

    override fun providePresenter(): ThemeFragmentPresenter {
        return ThemeFragmentPresenter()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (themeListener != null) {
                themeListener!!.onChangePrimaryDarkColor(
                    primaryDarkColor,
                    theme == R.style.ThemeLight
                )
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val productKey = data.getStringExtra(BundleConstant.ITEM)
            productKey?.let {
                when (it) {
                    "placeholder" -> setTheme(getString(R.string.amlod_theme_mode))
                    "placeholder" -> setTheme(getString(R.string.mid_night_blue_theme_mode))
                    "placeholder" -> setTheme(getString(R.string.bluish_theme))    
                }
            }
        }
    }

    companion object {
        fun newInstance(style: Int): ThemeFragment {
            val fragment = ThemeFragment()
            fragment.arguments = Bundler.start()
                .put(BundleConstant.ITEM, style)
                .end()
            return fragment
        }
    }

    private fun setTheme() {
        when (theme) {
            R.style.ThemeLight -> setTheme(getString(R.string.light_theme_mode))
            R.style.ThemeDark -> setTheme(getString(R.string.dark_theme_mode))
            R.style.ThemeAmlod -> applyAmlodTheme()
            R.style.ThemeBluish -> applyBluishTheme()
            R.style.ThemeMidnight -> applyMidnightTheme()
        }
    }

    private fun applyBluishTheme() {
        if (!isGoogleSupported()) return
        if (PrefGetter.isBluishEnabled || PrefGetter.isProEnabled) {
            setTheme(getString(R.string.bluish_theme))
        } else {
            PremiumActivity.startActivity(requireContext())
        }
    }

    private fun applyAmlodTheme() {
        if (!isGoogleSupported()) return
        if (PrefGetter.isAmlodEnabled || PrefGetter.isProEnabled) {
            setTheme(getString(R.string.amlod_theme_mode))
        } else {
            PremiumActivity.startActivity(requireContext())
        }
    }

    private fun applyMidnightTheme() {
        if (!isGoogleSupported()) return
        if (PrefGetter.isProEnabled || PrefGetter.isAllFeaturesUnlocked) {
            setTheme(getString(R.string.mid_night_blue_theme_mode))
        } else {
            PremiumActivity.startActivity(requireContext())
        }
    }

    private fun setTheme(theme: String) {
        PrefHelper.putAny(mTheme, theme)
        themeListener?.onThemeApplied()
    }

    private fun isPremiumTheme(): Boolean =
        theme != R.style.ThemeLight && theme != R.style.ThemeDark

    private fun isGoogleSupported(): Boolean {
        if (AppHelper.isGoogleAvailable(requireContext())) {
            return true
        }
        return false
    }
}
