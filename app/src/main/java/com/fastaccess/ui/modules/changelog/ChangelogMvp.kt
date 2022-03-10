package com.fastaccess.ui.modules.changelog

import com.fastaccess.ui.base.mvp.BaseMvp.FAView

/**
 * Created by Kosh on 28 May 2017, 10:53 AM
 */
interface ChangelogMvp {
    interface View : FAView {
        fun onChangelogLoaded(html: String?)
    }

    interface Presenter {
        fun onLoadChangelog()
    }
}