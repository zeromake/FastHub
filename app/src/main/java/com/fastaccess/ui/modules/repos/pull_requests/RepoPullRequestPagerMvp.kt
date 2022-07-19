package com.fastaccess.ui.modules.repos.pull_requests

import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.repos.RepoPagerMvp.TabsBadgeListener

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */
interface RepoPullRequestPagerMvp {
    interface View : FAView, TabsBadgeListener {
//        @get:IntRange(from = 1)
        val currentItem: Int
        fun onScrolled(isUp: Boolean)
    }

    interface Presenter : FAPresenter
}