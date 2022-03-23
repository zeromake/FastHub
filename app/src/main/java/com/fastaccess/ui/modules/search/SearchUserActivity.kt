package com.fastaccess.ui.modules.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.CheckBox
import androidx.core.widget.addTextChangedListener
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.InputHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.search.repos.SearchReposFragment
import com.fastaccess.ui.widgets.FontAutoCompleteEditText
import com.fastaccess.utils.setOnThrottleClickListener

class SearchUserActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    private val forkCheckBox: CheckBox by lazy { viewFind(R.id.forkCheckBox)!! }
    val clear: View by lazy { viewFind(R.id.clear)!! }
    val searchEditText: FontAutoCompleteEditText by lazy { viewFind(R.id.searchEditText)!! }

    @State
    var username = ""

    @State
    var searchTerm = ""

    fun onTextChange(str: Editable) {
        searchTerm = str.toString()
        if (searchTerm.isEmpty()) {
            AnimHelper.animateVisibility(clear, false)
        } else {
            AnimHelper.animateVisibility(clear, true)
        }
    }

    fun onSearchClicked() {
        searchTerm = searchEditText.text.toString()
        makeSearch()
    }

    fun checkBoxClicked() {
        onSearchClicked()
    }

    fun onEditor(): Boolean {
        onSearchClicked()
        return true
    }

    internal fun onClear() {
        searchEditText.setText("")
    }

    override fun layout(): Int = R.layout.activity_search_user

    override val isTransparent: Boolean = false

    override fun canBack(): Boolean = true

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override val isSecured: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchEditText.addTextChangedListener(
            { _, _, _, _ -> },
            { _, _, _, _ -> }
        ) {
            onTextChange(it!!)
        }
        searchEditText.setOnEditorActionListener { _, _, _ ->
            onEditor()
        }

        listOf(
            clear,
            viewFind(R.id.search)!!,
            viewFind(R.id.forkCheckBox)!!
        ).setOnThrottleClickListener {
            when (it.id) {
                R.id.clear -> onClear()
                R.id.search -> onSearchClicked()
                R.id.forkCheckBox -> checkBoxClicked()
            }
        }

        if (savedInstanceState == null) {
            val args = intent.extras!!
            username = args.getString(USERNAME)!!
            if (InputHelper.isEmpty(username)) {
                finish()
                return
            }
            searchTerm = args.getString(SEARCH_TERM)!!
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.containerFragment,
                    SearchReposFragment.newInstance(),
                    "SearchReposFragment"
                )
                .commit()
        }
        searchEditText.setText(searchTerm)
        onSearchClicked()
    }

    private fun makeSearch() {
        val query = "user:$username $searchTerm fork:${forkCheckBox.isChecked}"
        getFragment()?.onQueueSearch(query)
    }

    private fun getFragment() = AppHelper.getFragmentByTag(
        supportFragmentManager,
        "SearchReposFragment"
    ) as? SearchReposFragment?

    companion object {
        const val USERNAME = "username"
        const val SEARCH_TERM = "search"

        fun getIntent(context: Context, username: String, searchTerm: String?): Intent {
            val intent = Intent(context, SearchUserActivity::class.java)
            intent.putExtra(USERNAME, username)
            intent.putExtra(SEARCH_TERM, searchTerm)
            return intent
        }
    }
}