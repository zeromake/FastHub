package com.fastaccess.ui.modules.user

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.profile.ProfilePagerMvp
import com.fastaccess.ui.modules.repos.RepoPagerMvp.TabsBadgeListener
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter

/**
 * Created by Kosh on 04 Dec 2016, 1:11 PM
 */
interface UserPagerMvp {
    interface View : FAView, ProfilePagerMvp.View, TabsBadgeListener {
        fun onInitOrg(isMember: Boolean)
        fun onUserBlocked()
        fun onInvalidateMenu()
        fun onUserUnBlocked()
    }

    interface Presenter : FAPresenter {
        fun onCheckBlocking(login: String)
        fun checkOrgMembership(org: String)
        fun onBlockUser(login: String)
        fun onUnblockUser(login: String)
    }
}