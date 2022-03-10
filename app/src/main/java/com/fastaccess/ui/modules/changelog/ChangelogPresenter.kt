package com.fastaccess.ui.modules.changelog

import com.fastaccess.App
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.gson.ToGsonProvider.getChangelog
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 28 May 2017, 10:53 AM
 */
class ChangelogPresenter : BasePresenter<ChangelogMvp.View>(), ChangelogMvp.Presenter {
    var html: String? = null
        private set

    override fun onLoadChangelog() {
        manageDisposable(RxHelper.getObservable(getChangelog(App.getInstance()))
            .subscribe({ s: String? ->
                html = s
                sendToView { view -> view.onChangelogLoaded(html) }
            }) {
                sendToView { view -> view.onChangelogLoaded(null) }
            })
    }
}