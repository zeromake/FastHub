package com.fastaccess.ui.modules.profile.org

import android.os.Bundle
import com.fastaccess.data.dao.model.User
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView

/**
 * Created by Kosh on 03 Dec 2016, 7:59 AM
 */
interface OrgProfileOverviewMvp {
    interface View : FAView {
        fun onInitViews(userModel: User?)
    }

    interface Presenter : FAPresenter {
        fun onFragmentCreated(bundle: Bundle?)
        fun onWorkOffline(login: String)
        var login: String?
    }
}