package com.fastaccess.ui.modules.editor.popup

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import java.io.File

/**
 * Created by Kosh on 15 Apr 2017, 9:06 PM
 */
interface EditorLinkImageMvp {
    interface EditorLinkCallback {
        fun onAppendLink(title: String?, link: String?, isLink: Boolean)
    }

    interface View : FAView {
        fun onUploaded(title: String?, link: String?)
    }

    interface Presenter {
        fun onSubmit(title: String?, file: File)
    }
}