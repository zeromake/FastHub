package com.fastaccess.ui.modules.repos.code.commit.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel.Companion.buildForCommit
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.model.Commit
import com.fastaccess.helper.ActivityHelper.shareUrl
import com.fastaccess.helper.ActivityHelper.startCustomTab
import com.fastaccess.helper.AppHelper.copyToClipboard
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.provider.scheme.LinkParserHelper.isEnterprise
import com.fastaccess.provider.scheme.SchemeParser.launchUri
import com.fastaccess.provider.timeline.HtmlHelper.htmlIntoTextView
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment
import com.fastaccess.ui.modules.repos.RepoPagerActivity.Companion.startRepoPager
import com.fastaccess.ui.modules.repos.code.commit.details.comments.CommitCommentsFragment
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.fastaccess.ui.widgets.ViewPagerView
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.dialog.MessageDialogView.Companion.newInstance
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */
class CommitPagerActivity : BaseActivity<CommitPagerMvp.View, CommitPagerPresenter>(),
    CommitPagerMvp.View {
    val avatarLayout: AvatarLayout? by lazy { decorViewFindViewById(R.id.avatarLayout) }
    val title: FontTextView? by lazy { decorViewFindViewById(R.id.headerTitle) }
    val size: FontTextView? by lazy { decorViewFindViewById(R.id.size) }
    val date: FontTextView? by lazy { decorViewFindViewById(R.id.date) }
    val tabs: TabLayout? by lazy { decorViewFindViewById(R.id.tabs) }
    val pager: ViewPagerView? by lazy { decorViewFindViewById(R.id.pager) }
    val fab: FloatingActionButton? by lazy { decorViewFindViewById(R.id.fab) }
    val changes: FontTextView? by lazy { decorViewFindViewById(R.id.changes) }
    val addition: FontTextView? by lazy { decorViewFindViewById(R.id.addition) }
    val deletion: FontTextView? by lazy { decorViewFindViewById(R.id.deletion) }
//    val coordinatorLayout: CoordinatorLayout? by lazy { decorViewFindViewById(R.id.deletion) }
    val detailsIcon: View? by lazy { decorViewFindViewById(R.id.deletion) }

    private var commentEditorFragment: CommentEditorFragment? = null
    private fun onTitleClick() {
        if (presenter!!.commit != null && !isEmpty(presenter!!.commit!!.gitCommit.message)) newInstance(
            String.format("%s/%s", presenter!!.login, presenter!!.repoId),
            presenter!!.commit!!.gitCommit.message!!, isMarkDown = true, hideCancel = false
        )
            .show(supportFragmentManager, MessageDialogView.TAG)
    }

    override fun layout(): Int {
        return R.layout.commit_pager_activity
    }

    override val isTransparent: Boolean = true

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): CommitPagerPresenter {
        return CommitPagerPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        decorViewFindViewById<View>(R.id.detailsIcon)!!.setOnThrottleClickListener {
            onTitleClick()
        }

        fab!!.hide()
        commentEditorFragment =
            supportFragmentManager.findFragmentById(R.id.commentFragment) as CommentEditorFragment?
        setTitle("")
        if (savedInstanceState == null) {
            presenter!!.onActivityCreated(intent)
        } else {
            if (presenter!!.isApiCalled) onSetup()
        }
        if (presenter!!.showToRepoBtn()) showNavToRepoItem()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        menu.findItem(R.id.browser).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.findItem(R.id.copyUrl).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.findItem(R.id.copySha).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.findItem(R.id.share).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onNavToRepoClicked()
                return true
            }
            R.id.share -> {
                if (presenter!!.commit != null) shareUrl(this, presenter!!.commit!!.htmlUrl)
                return true
            }
            R.id.browser -> {
                if (presenter!!.commit != null) startCustomTab(this, presenter!!.commit!!.htmlUrl)
                return true
            }
            R.id.copyUrl -> {
                if (presenter!!.commit != null) copyToClipboard(this, presenter!!.commit!!.htmlUrl)
                return true
            }
            R.id.copySha -> {
                if (presenter!!.commit != null) copyToClipboard(this, presenter!!.commit!!.sha)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSetup() {
        hideProgress()
        if (presenter!!.commit == null) {
            return
        }
        invalidateOptionsMenu()
        val commit = presenter!!.commit
        val login =
            if (commit!!.author != null) commit.author.login else commit.gitCommit.author!!.name
        val avatar = if (commit.author != null) commit.author.avatarUrl else null
        val dateValue = commit.gitCommit.author!!.date
        htmlIntoTextView(title!!, commit.gitCommit.message!!, title!!.width)
        setTaskName(commit.login + "/" + commit.repoId + " - Commit " + commit.sha.take(5))
        detailsIcon!!.visibility = View.VISIBLE
        size!!.visibility = View.GONE
        date!!.text = builder()
            .bold(presenter!!.repoId!!)
            .append(" ")
            .append(" ")
            .append(getTimeAgo(dateValue))
        avatarLayout!!.setUrl(avatar, login, false, isEnterprise(commit.htmlUrl))
        addition!!.text =
            (if (commit.stats != null) commit.stats.additions.toString() else 0.toString())
        deletion!!.text =
            (if (commit.stats != null) commit.stats.deletions.toString() else 0.toString())
        changes!!.text = (if (commit.files != null) commit.files.size.toString() else 0.toString())
        pager!!.adapter =
            FragmentsPagerAdapter(supportFragmentManager, buildForCommit(this, commit))
        tabs!!.setupWithViewPager(pager)
        pager!!.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                hideShowFab()
            }
        })
        hideShowFab()
        val tabOne = tabs!!.getTabAt(0)
        val tabTwo = tabs!!.getTabAt(1)
        if (tabOne != null && commit.files != null) {
            tabOne.text = getString(R.string.files) + " (" + commit.files.size + ")"
        }
        if (tabTwo != null && commit.gitCommit != null && commit.gitCommit.commentCount > 0) {
            tabTwo.text = getString(R.string.comments) + " (" + commit.gitCommit.commentCount + ")"
        }
        tabs!!.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(pager) {
            override fun onTabReselected(tab: TabLayout.Tab) {
                super.onTabReselected(tab)
                onScrollTop(tab.position)
            }
        })
    }

    override fun onScrollTop(index: Int) {
        if (pager == null || pager!!.adapter == null) return
        val fragment: Fragment = pager!!.adapter!!
            .instantiateItem(pager!!, index) as BaseFragment<*, *>
        if (fragment is BaseFragment<*, *>) {
            fragment.onScrollTop(index)
        }
    }

    override fun onFinishActivity() {
        hideProgress()
        finish()
    }

    override fun onAddComment(newComment: Comment) {
        val fragment = commitCommentsFragment
        fragment?.addComment(newComment)
    }

    override val login: String?
        get() = presenter!!.login
    override val repoId: String?
        get() = presenter!!.repoId

    override fun onNavToRepoClicked() {
        val nameParser = NameParser("")
        nameParser.name = presenter!!.repoId
        nameParser.username = presenter!!.login
        nameParser.isEnterprise = isEnterprise
        startRepoPager(this, nameParser)
        finish()
    }

    override fun onSendActionClicked(text: String, bundle: Bundle?) {
        val fragment = commitCommentsFragment
        fragment?.onHandleComment(text, bundle)
    }

    override fun onTagUser(username: String) {
        commentEditorFragment!!.onAddUserName(username)
    }

    override fun onCreateComment(text: String, bundle: Bundle?) {}
    override fun onClearEditText() {
        if (commentEditorFragment != null) commentEditorFragment!!.commentText.setText(
            ""
        )
    }

    override fun getNamesToTag(): ArrayList<String> {
        val fragment = commitCommentsFragment
        return fragment?.namesToTags ?: ArrayList()
    }

    private fun hideShowFab() {
        if (pager!!.currentItem == 1) {
            supportFragmentManager.beginTransaction().show(commentEditorFragment!!).commit()
        } else {
            supportFragmentManager.beginTransaction().hide(commentEditorFragment!!).commit()
        }
    }

    private val commitCommentsFragment: CommitCommentsFragment?
        get() = if (pager != null && pager!!.adapter != null) pager!!.adapter!!
            .instantiateItem(pager!!, 1) as CommitCommentsFragment else null

    companion object {
        @JvmOverloads
        fun createIntent(
            context: Context, repoId: String, login: String,
            sha: String, showRepoBtn: Boolean = false,
            isEnterprise: Boolean = false
        ): Intent {
            val intent = Intent(context, CommitPagerActivity::class.java)
            intent.putExtras(
                start()
                    .put(BundleConstant.ID, sha)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TWO, repoId)
                    .put(BundleConstant.EXTRA_THREE, showRepoBtn)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                    .end()
            )
            return intent
        }

        @JvmStatic
        fun createIntentForOffline(context: Context, commitModel: Commit) {
            launchUri(context, Uri.parse(commitModel.htmlUrl))
        }
    }
}