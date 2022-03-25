package com.fastaccess.ui.modules.login

import android.content.Intent
import android.net.Uri
import com.fastaccess.data.dao.AccessTokenModel
import com.fastaccess.data.dao.model.Login
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView

/**
 * Created by Kosh on 09 Nov 2016, 9:41 PM
 */
interface LoginMvp {
    interface View : FAView {
        fun onRequire2Fa()
        fun onEmptyUserName(isEmpty: Boolean)
        fun onEmptyPassword(isEmpty: Boolean)
        fun onEmptyEndpoint(isEmpty: Boolean)
        fun onSuccessfullyLoggedIn(extraLogin: Boolean)
    }

    interface Presenter : FAPresenter {
        val authorizationUrl: Uri
        fun onHandleAuthIntent(intent: Intent?)
        fun onTokenResponse(response: AccessTokenModel?)
        fun onUserResponse(response: Login?)
        fun login(
            username: String, password: String,
            twoFactorCode: String?, isBasicAuth: Boolean,
            endpoint: String?
        )
    }
}