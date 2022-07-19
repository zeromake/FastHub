package com.fastaccess.ui.modules.about

import android.os.Bundle
import com.fastaccess.provider.theme.ThemeEngine

class CommonLibsActivity : com.mikepenz.aboutlibraries.ui.LibsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeEngine.apply(this)
        super.onCreate(savedInstanceState)
    }
}
