package com.fastaccess.ui.modules.repos.code.commit.viewer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.CommitFileModel
import com.fastaccess.helper.ActivityHelper.checkAndRequestReadWritePermission
import com.fastaccess.helper.ActivityHelper.shareUrl
import com.fastaccess.helper.AppHelper.copyToClipboard
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.ViewHelper.getPatchAdditionColor
import com.fastaccess.helper.ViewHelper.getPatchDeletionColor
import com.fastaccess.helper.ViewHelper.getPatchRefColor
import com.fastaccess.provider.rest.RestProvider.downloadFile
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.EmptyPresenter
import com.fastaccess.ui.modules.code.CodeViewerActivity.Companion.createIntent
import com.fastaccess.ui.widgets.DiffLineSpan.Companion.getSpannable
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder

/**
 * Created by Kosh on 24 Apr 2017, 2:53 PM
 */
class FullCommitFileActivity : BaseActivity<BaseMvp.FAView, EmptyPresenter>() {
    @State
    var commitFileModel: CommitFileModel? = null
    val textView: FontTextView? by lazy { decorViewFindViewById(R.id.textView) }
    val changes: FontTextView? by lazy { decorViewFindViewById(R.id.changes) }
    val addition: FontTextView? by lazy { decorViewFindViewById(R.id.addition) }
    val deletion: FontTextView? by lazy { decorViewFindViewById(R.id.deletion) }
    val status: FontTextView? by lazy { decorViewFindViewById(R.id.status) }
    val changesText: String by lazy { resources.getString(R.string.changes) }
    val additionText: String by lazy { resources.getString(R.string.addition) }
    val deletionText: String by lazy { resources.getString(R.string.deletion) }
    val statusText: String by lazy { resources.getString(R.string.status) }

    override fun layout(): Int {
        return R.layout.commit_file_full_layout
    }

    override val isTransparent: Boolean = false

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): EmptyPresenter {
        return EmptyPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            commitFileModel = intent.extras!!.getParcelable(BundleConstant.ITEM)
        }
        if (commitFileModel == null || commitFileModel!!.patch == null) {
            finish()
            return
        }
        changes!!.text = builder()
            .append(changesText)
            .append("\n")
            .bold(commitFileModel!!.changes.toString())
        addition!!.text = builder()
            .append(additionText)
            .append("\n")
            .bold(commitFileModel!!.additions.toString())
        deletion!!.text = builder()
            .append(deletionText)
            .append("\n")
            .bold(commitFileModel!!.deletions.toString())
        status!!.text = builder()
            .append(statusText)
            .append("\n")
            .bold(commitFileModel!!.status.toString())
        title = Uri.parse(commitFileModel!!.filename).lastPathSegment
        textView!!.text = getSpannable(
            commitFileModel!!.patch,
            getPatchAdditionColor(this),
            getPatchDeletionColor(this),
            getPatchRefColor(this), false
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.commit_row_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open -> {
                startActivity(
                    createIntent(
                        this,
                        commitFileModel!!.contentsUrl!!,
                        commitFileModel!!.blobUrl!!
                    )
                )
                true
            }
            R.id.share -> {
                shareUrl(this, commitFileModel!!.blobUrl!!)
                true
            }
            R.id.download -> {
                if (checkAndRequestReadWritePermission(this)) {
                    downloadFile(this, commitFileModel!!.rawUrl!!)
                }
                true
            }
            R.id.copy -> {
                copyToClipboard(this, commitFileModel!!.blobUrl!!)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun start(context: Context, fileModel: CommitFileModel) {
            val starter = Intent(context, FullCommitFileActivity::class.java)
            starter.putExtras(
                start()
                    .put(BundleConstant.ITEM, fileModel)
                    .end()
            )
            context.startActivity(starter)
        }
    }
}