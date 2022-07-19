package com.fastaccess.ui.modules.login.chooser

import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

class LoginChooserPresenter : BasePresenter<LoginChooserMvp.View>() {
    init {
        manageObservable(
            LoginDao.getAccounts().toList()
                .toObservable()
                .doOnNext {
                    sendToView { view ->
                        view.onAccountsLoaded(it)
                    }
                })
    }
}