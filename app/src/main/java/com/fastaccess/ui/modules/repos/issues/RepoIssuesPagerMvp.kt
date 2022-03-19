package com.fastaccess.ui.modules.repos.issues

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.repos.RepoPagerMvp.TabsBadgeListener
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */
interface RepoIssuesPagerMvp {
    interface View : FAView, TabsBadgeListener {
        fun onAddIssue()
        fun setCurrentItem(index: Int, refresh: Boolean)
        fun onChangeIssueSort(isLastUpdated: Boolean)
        val currentItem: Int
        fun onScrolled(isUp: Boolean)
    }

    interface Presenter : FAPresenter
}