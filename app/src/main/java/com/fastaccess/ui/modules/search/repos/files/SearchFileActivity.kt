package com.fastaccess.ui.modules.search.repos.files

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper.animateVisibility
import com.fastaccess.helper.BundleConstant
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.search.code.SearchCodeFragment
import com.fastaccess.ui.widgets.FontEditText
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.utils.setOnThrottleClickListener

class SearchFileActivity : BaseActivity<SearchFileMvp.View, SearchFilePresenter>(),
    SearchFileMvp.View {
    val searchEditText: FontEditText? by lazy { viewFind(R.id.searchEditText) }
    val clear: ForegroundImageView? by lazy { viewFind(R.id.clear) }
    val searchOptions: Spinner? by lazy { viewFind(R.id.searchOptions) }

    private var onSpinnerTouched = false
    private var searchCodeFragment: SearchCodeFragment? = null

    private fun onTouch(): Boolean {
        onSpinnerTouched = true
        return false
    }

    fun onOptionSelected() {
        if (onSpinnerTouched) {
            onSearch()
        }
    }

    fun onTextChange(s: Editable) {
        val text = s.toString()
        if (text.isEmpty()) {
            animateVisibility(clear, false)
        } else {
            animateVisibility(clear, true)
        }
    }

    fun onEditor(): Boolean {
        onSearch()
        return true
    }

    fun onClear(view: View) {
        if (view.id == R.id.clear) {
            searchEditText!!.setText("")
        }
    }

    private fun onSearchClicked() {
        onSearch()
    }

    override fun layout(): Int {
        return R.layout.activity_search_file
    }

    override val isTransparent: Boolean = false

    override fun canBack(): Boolean {
        return true
    }

    override val isSecured: Boolean = false

    override fun providePresenter(): SearchFilePresenter {
        return SearchFilePresenter()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchOptions!!.setOnTouchListener { _, _ ->
            onTouch()
        }
        searchOptions!!.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onOptionSelected()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        searchEditText!!.addTextChangedListener(
            { _, _, _, _ -> },
            { _, _, _, _ -> }
        ) {
            onTextChange(it!!)
        }
        searchEditText!!.setOnEditorActionListener { _, _, _ ->
            onEditor()
        }
        clear!!.setOnThrottleClickListener {
            onClear(it)
        }
        viewFind<View>(R.id.search)!!.setOnThrottleClickListener {
            onSearchClicked()
        }

        presenter!!.onActivityCreated(intent.extras)
        searchCodeFragment =
            supportFragmentManager.findFragmentById(R.id.filesFragment) as SearchCodeFragment?
    }

    override fun onValidSearchQuery(query: String) {
        searchCodeFragment!!.onSetSearchQuery(query, false)
    }

    private fun onSearch() {
        presenter!!.onSearchClicked(searchEditText!!, searchOptions!!.selectedItemPosition == 0)
    }

    companion object {
        @JvmStatic
        fun createIntent(
            context: Context,
            login: String,
            repoId: String,
            isEnterprise: Boolean
        ): Intent {
            val intent = Intent(context, SearchFileActivity::class.java)
            intent.putExtra(BundleConstant.ID, repoId)
            intent.putExtra(BundleConstant.EXTRA, login)
            intent.putExtra(BundleConstant.IS_ENTERPRISE, isEnterprise)
            return intent
        }
    }
}