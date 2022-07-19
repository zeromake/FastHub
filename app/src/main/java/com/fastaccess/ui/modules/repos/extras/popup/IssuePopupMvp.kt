package com.fastaccess.ui.modules.repos.extras.popup

import com.fastaccess.ui.base.mvp.BaseMvp.FAView

/**
 * Created by Kosh on 27 May 2017, 1:55 PM
 */
interface IssuePopupMvp {
    interface View : FAView {
        fun onSuccessfullySubmitted()
    }

    interface Presenter {
        fun onSubmit(login: String, repoId: String, issueNumber: Int, text: String)
    }
}