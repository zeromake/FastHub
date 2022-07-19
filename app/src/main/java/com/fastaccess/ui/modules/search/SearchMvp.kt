package com.fastaccess.ui.modules.search

import android.widget.AutoCompleteTextView
import androidx.annotation.IntRange
import androidx.viewpager.widget.ViewPager
import com.fastaccess.data.entity.SearchHistory
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView

/**
 * Created by Kosh on 08 Dec 2016, 8:19 PM
 */
interface SearchMvp {
    interface View : FAView {
        fun onNotifyAdapter(query: SearchHistory?)
        fun onSetCount(count: Int, @IntRange(from = 0, to = 3) index: Int)
    }

    interface Presenter : FAPresenter {
        val hints: ArrayList<SearchHistory>
        fun onSearchClicked(viewPager: ViewPager, editText: AutoCompleteTextView)
    }
}
