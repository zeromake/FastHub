package com.fastaccess.ui.modules.settings.category

import android.os.Bundle
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.SettingsModel.SettingsType
import com.fastaccess.helper.BundleConstant
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.settings.category.SettingsCategoryFragment.SettingsCallback

class SettingsCategoryActivity : BaseActivity<FAView, SettingsCategoryPresenter>(),
    SettingsCallback {
    @State
    @SettingsType
    override var settingsType: Int = 0

    @State
    var title: String? = null

    @State
    var needRecreation = false
    override fun layout(): Int {
        return R.layout.activity_settings_category
    }

    override val isTransparent: Boolean = false
    override fun canBack(): Boolean {
        return true
    }

    override val isSecured: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        if (savedInstanceState == null) {
            val bundle = intent.extras
            title = bundle!!.getString(BundleConstant.EXTRA)
            settingsType = bundle.getInt(BundleConstant.ITEM)
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.settingsContainer,
                    SettingsCategoryFragment(),
                    SettingsCategoryFragment.TAG
                )
                .commit()
        }
        setTitle(title)
    }

    override fun providePresenter(): SettingsCategoryPresenter {
        return SettingsCategoryPresenter()
    }

    override fun onThemeChanged() {
        needRecreation = true
    }

    override fun onBackPressed() {
        if (needRecreation) {
            super.onThemeChanged()
            return
        }
        super.onBackPressed()
    }

}