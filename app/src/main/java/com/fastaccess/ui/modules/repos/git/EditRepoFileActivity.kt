package com.fastaccess.ui.modules.repos.git

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentManager
import com.fastaccess.R
import com.fastaccess.data.dao.EditRepoFileModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by kosh on 29/08/2017.
 */
class EditRepoFileActivity : BaseActivity<EditRepoFileMvp.View, EditRepoFilePresenter>(),
    EditRepoFileMvp.View {
    val markDownLayout: MarkDownLayout by lazy { viewFind(R.id.markDownLayout)!! }
    val editText: MarkdownEditText by lazy { viewFind(R.id.editText)!! }
    val description: TextInputLayout by lazy { viewFind(R.id.description)!! }
    val fileName: TextInputLayout by lazy { viewFind(R.id.fileName)!! }
    val commitHolder: View by lazy { viewFind(R.id.commitHolder)!! }


    override fun layout(): Int = R.layout.edit_repo_file_layout

    override val isTransparent: Boolean = false

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = true

    override fun providePresenter(): EditRepoFilePresenter = EditRepoFilePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        markDownLayout.markdownListener = this
        setToolbarIcon(R.drawable.ic_clear)
        if (savedInstanceState == null) {
            presenter.onInit(intent)
        }
        val path = presenter.model?.path
        if (!path.isNullOrBlank() && presenter.model?.fileName.isNullOrBlank()) {
            val name = Uri.parse(path)?.lastPathSegment
            title = name
            fileName.editText?.setText(name)
        } else if (!presenter.model?.fileName.isNullOrBlank()) {
            fileName.editText?.setText(presenter.model?.fileName)
            fileName.isEnabled = false
            title = presenter.model?.fileName
        }
        toolbar?.let {
            it.subtitle = "${presenter.model?.login}/${presenter.model?.repoId}"
        }
    }

    override fun onSetText(content: String?) {
        hideProgress()
        editText.setText(content)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            val text = editText.text
            if (presenter.fileContent == text.toString()
                && presenter.model?.fileName == fileName.editText?.text.toString()
            ) {
                showErrorMessage(getString(R.string.commit_file_required))
                return true
            }
            presenter.onSubmit(
                editText.text?.toString(),
                fileName.editText?.text?.toString(),
                description.editText?.text?.toString()
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (menu.findItem(R.id.submit) != null) {
            menu.findItem(R.id.submit).isEnabled = true
        }
        presenter.model?.isEdit?.let {
            menu.findItem(R.id.submit).setIcon(R.drawable.ic_done)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        markDownLayout.onAppendLink(title, link, isLink)
    }

    override fun getEditText(): EditText = editText

    override fun getSavedText(): CharSequence? = editText.savedText

    override fun fragmentManager(): FragmentManager = supportFragmentManager

    @SuppressLint("SetTextI18n")
    override fun onEmojiAdded(emoji: Emoji?) {
        markDownLayout.onEmojiAdded(emoji)
    }

    override fun onSetTextError(isEmpty: Boolean) {
        editText.error = if (isEmpty) getString(R.string.required_field) else null
    }

    override fun onSetDescriptionError(isEmpty: Boolean) {
        description.error = if (isEmpty) getString(R.string.required_field) else null
    }

    override fun onSetFilenameError(isEmpty: Boolean) {
        fileName.error = if (isEmpty) getString(R.string.required_field) else null
    }

    override fun onSuccessfullyCommitted() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
//        const val EDIT_RQ = 2017
//
//        @Deprecated("use registerForActivityResult")
//        fun startForResult(activity: Activity, model: EditRepoFileModel, isEnterprise: Boolean) {
//            val bundle = Bundler.start()
//                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
//                    .put(BundleConstant.ITEM, model)
//                    .end()
//            val intent = Intent(activity, EditRepoFileActivity::class.java)
//            intent.putExtras(bundle)
//            activity.startActivityForResult(intent, EDIT_RQ)
//        }
//
//        @Deprecated("use registerForActivityResult")
//        fun startForResult(fragment: Fragment, model: EditRepoFileModel, isEnterprise: Boolean) {
//            val bundle = Bundler.start()
//                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
//                    .put(BundleConstant.ITEM, model)
//                    .end()
//            val intent = Intent(fragment.context, EditRepoFileActivity::class.java)
//            intent.putExtras(bundle)
//            fragment.startActivityForResult(intent, EDIT_RQ)
//        }

        fun startForResult(
            context: Context,
            launcher: ActivityResultLauncher<Intent>,
            model: EditRepoFileModel,
            isEnterprise: Boolean
        ) {
            val bundle = Bundler.start()
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .put(BundleConstant.ITEM, model)
                .end()
            val intent = Intent(context, EditRepoFileActivity::class.java)
            intent.putExtras(bundle)
            launcher.launch(intent)
        }
    }
}