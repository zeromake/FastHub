package com.fastaccess.ui.modules.repos.code

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.repos.RepoPagerMvp.TabsBadgeListener
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */
interface RepoCodePagerMvp {
    interface View : FAView, TabsBadgeListener {
        fun canPressBack(): Boolean
        fun onBackPressed()
    }

    interface Presenter : FAPresenter
}