package com.fastaccess.ui.modules.editor.comment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.transition.TransitionManager
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.editor.EditorActivity
import com.fastaccess.ui.modules.editor.emoji.EmojiMvp
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText
import com.fastaccess.utils.setOnThrottleClickListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar

/**
 * Created by kosh on 21/08/2017.
 */
class CommentEditorFragment : BaseFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(),
    MarkDownLayout.MarkdownListener,
    EmojiMvp.EmojiCallback, EditorLinkImageMvp.EditorLinkCallback {

    lateinit var commentBox: View
    lateinit var markDownLayout: MarkDownLayout
    lateinit var commentText: MarkdownEditText
    private lateinit var markdownBtnHolder: View
    lateinit var sendComment: View
    private lateinit var toggleButtons: View
    private var commentListener: CommentListener? = null
    private var keyboardListener: Unregistrar? = null

    internal fun onComment() {
        if (!InputHelper.isEmpty(getEditText())) {
            commentListener?.onSendActionClicked(
                InputHelper.toString(getEditText()),
                arguments?.getBundle(BundleConstant.ITEM)
            )
            ViewHelper.hideKeyboard(getEditText())
            arguments = null
        }
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val text = it.data?.extras?.getCharSequence(BundleConstant.EXTRA) ?: ""
            getEditText().setText(text)
            getEditText().setSelection(getEditText().text.length)
        }
    }

    private fun onExpandScreen() {
        val intent = Intent(context, EditorActivity::class.java)
        intent.putExtras(
            Bundler.start()
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.FOR_RESULT_EXTRA)
                .put(BundleConstant.EXTRA, getEditText().text.toString())
                .putStringArrayList("participants", commentListener?.getNamesToTag())
                .end()
        )
        launcher.launch(intent)
    }

    private fun onToggleButtons(v: View) {
        TransitionManager.beginDelayedTransition((view as ViewGroup?)!!)
        v.isActivated = !v.isActivated
        markdownBtnHolder.visibility =
            if (markdownBtnHolder.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is CommentListener) {
            commentListener = parentFragment as CommentListener
        } else if (context is CommentListener) {
            commentListener = context
        }
    }

    override fun onDetach() {
        commentListener = null
        super.onDetach()
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun fragmentLayout(): Int = R.layout.comment_box_layout

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        this.commentBox = view.findViewById(R.id.commentBox)
        this.markDownLayout = view.findViewById(R.id.markdDownLayout)
        this.commentText = view.findViewById(R.id.commentText)
        this.markdownBtnHolder = view.findViewById(R.id.markdownBtnHolder)
        this.sendComment = view.findViewById(R.id.sendComment)
        this.toggleButtons = view.findViewById(R.id.toggleButtons)
        this.sendComment.setOnThrottleClickListener {
            this.onComment()
        }
        view.findViewById<View>(R.id.fullScreenComment).setOnThrottleClickListener {
            this.onExpandScreen()
        }
        this.toggleButtons.setOnThrottleClickListener {
            this.onToggleButtons(it)
        }
        arguments?.let {
            val hideSendButton = it.getBoolean(BundleConstant.YES_NO_EXTRA)
            if (hideSendButton) {
                sendComment.visibility = View.GONE
            }
        }
        markDownLayout.markdownListener = this
        if (savedInstanceState == null) {
            arguments?.getBundle(BundleConstant.ITEM)?.getString(BundleConstant.EXTRA)
                ?.let { commentText.setText(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        keyboardListener = KeyboardVisibilityEvent.registerEventListener(activity) {
            TransitionManager.beginDelayedTransition((view as ViewGroup?)!!)
            toggleButtons.isActivated = it
            markdownBtnHolder.visibility = if (!it) View.GONE else View.VISIBLE
        }
    }

    override fun onStop() {
        keyboardListener?.unregister()
        super.onStop()
    }

    override fun getEditText(): EditText = commentText

    override fun fragmentManager(): FragmentManager = childFragmentManager

    override fun getSavedText(): CharSequence? = commentText.savedText

    override fun onEmojiAdded(emoji: Emoji?) = markDownLayout.onEmojiAdded(emoji)

    @SuppressLint("SetTextI18n")
    fun onCreateComment(text: String, bundle: Bundle?) {
        arguments = Bundler.start().put(BundleConstant.ITEM, bundle).end()
        commentText.setText("${if (commentText.text.isNullOrBlank()) "" else "${commentText.text} "}$text")
        getEditText().setSelection(getEditText().text.length)
        commentText.requestFocus()
        ViewHelper.showKeyboard(commentText)
    }

    fun onAddUserName(username: String) {
        getEditText().setText(
            if (getEditText().text.isNullOrBlank()) {
                "@$username"
            } else {
                "${getEditText().text} @$username"
            }
        )
        getEditText().setSelection(getEditText().text.length)
    }

    override fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        markDownLayout.onAppendLink(title, link, isLink)
    }

    interface CommentListener {
        fun onCreateComment(text: String, bundle: Bundle?) {}
        fun onSendActionClicked(text: String, bundle: Bundle?)
        fun onTagUser(username: String)
        fun onClearEditText()
        fun getNamesToTag(): ArrayList<String>?
    }

    companion object {
        fun newInstance(bundle: Bundle?): CommentEditorFragment {
            val fragment = CommentEditorFragment()
            bundle?.let {
                fragment.arguments = Bundler.start().put(BundleConstant.ITEM, bundle).end()
            }
            return fragment
        }
    }
}