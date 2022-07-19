package com.fastaccess.ui.modules.search.repos.files

import android.os.Bundle
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.widgets.FontEditText

interface SearchFileMvp {
    interface View : FAView {
        fun onValidSearchQuery(query: String)
    }

    interface Presenter : FAPresenter {
        fun onSearchClicked(editText: FontEditText, inPath: Boolean)
        fun onActivityCreated(extras: Bundle?)
    }
}