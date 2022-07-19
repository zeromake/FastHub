package com.fastaccess.ui.modules.repos.code.releases

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.NameParser
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.EmptyPresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity.Companion.startRepoPager
import com.fastaccess.ui.modules.repos.code.releases.RepoReleasesFragment.Companion.newInstance

/**
 * Created by Kosh on 25 May 2017, 7:13 PM
 */
class ReleasesListActivity : BaseActivity<BaseMvp.FAView, EmptyPresenter>() {
    @State
    var repoId: String? = null

    @State
    var login: String? = null

    override fun layout(): Int {
        return R.layout.activity_fragment_layout
    }

    override val isTransparent: Boolean = true

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): EmptyPresenter {
        return EmptyPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            if (intent == null || intent.extras == null) {
                finish()
            } else {
                val bundle = intent.extras
                repoId = bundle!!.getString(BundleConstant.ID)
                login = bundle.getString(BundleConstant.EXTRA)
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.container, newInstance(
                            repoId!!, login!!, bundle.getString(BundleConstant.EXTRA_THREE),
                            bundle.getLong(BundleConstant.EXTRA_TWO)
                        )
                    )
                    .commit()
                setTaskName(repoId + "/" + login + " " + getString(R.string.releases))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val parser = NameParser("")
            parser.name = repoId
            parser.username = login
            parser.isEnterprise = isEnterprise
            startRepoPager(this, parser)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun getIntent(context: Context, username: String, repoId: String): Intent {
            val intent = Intent(context, ReleasesListActivity::class.java)
            intent.putExtras(
                start().put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, username)
                    .end()
            )
            return intent
        }

        fun getIntent(
            context: Context, username: String, repoId: String,
            tag: String, isEnterprise: Boolean
        ): Intent {
            val intent = Intent(context, ReleasesListActivity::class.java)
            intent.putExtras(
                start().put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, username)
                    .put(BundleConstant.EXTRA_THREE, tag)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                    .end()
            )
            return intent
        }

        fun getIntent(
            context: Context, username: String, repoId: String,
            id: Long, isEnterprise: Boolean
        ): Intent {
            val intent = Intent(context, ReleasesListActivity::class.java)
            intent.putExtras(
                start().put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, username)
                    .put(BundleConstant.EXTRA_TWO, id)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                    .end()
            )
            return intent
        }
    }
}