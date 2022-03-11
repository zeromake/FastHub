package com.fastaccess.ui.modules.editor.popup

import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp.EditorLinkCallback
import butterknife.BindView
import com.fastaccess.R
import com.google.android.material.textfield.TextInputLayout
import com.fastaccess.ui.widgets.FontButton
import android.os.Bundle
import android.content.Intent
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.dmoral.toasty.Toasty
import com.fastaccess.App
import butterknife.OnClick
import com.fastaccess.helper.*
import java.io.File

/**
 * Created by Kosh on 15 Apr 2017, 9:14 PM
 */
class EditorLinkImageDialogFragment :
    BaseDialogFragment<EditorLinkImageMvp.View, EditorLinkImagePresenter>(),
    EditorLinkImageMvp.View {
    private var editorCallback: EditorLinkCallback? = null

    lateinit var title: TextInputLayout

    lateinit var link: TextInputLayout

    lateinit var select: FontButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!
        this.title = root.findViewById(R.id.title)
        this.link = root.findViewById(R.id.link)
        this.select = root.findViewById(R.id.select)


        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is EditorLinkCallback) {
            editorCallback = (parentFragment as EditorLinkCallback)
        } else if (context is EditorLinkCallback) {
            editorCallback = context
        }
    }

    override fun onDetach() {
        editorCallback = null
        super.onDetach()
    }

    override fun onUploaded(title: String?, link: String?) {
        hideProgress()
        if (editorCallback != null) {
            editorCallback?.onAppendLink(title, link?.replace("http:", "https:"), isLink())
        }
        dismiss()
    }

    override fun fragmentLayout(): Int {
        return R.layout.markdown_link_image_dialog_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        select.visibility = if (isLink()) View.GONE else View.VISIBLE
        if (savedInstanceState == null) {
            title.editText!!.setText(requireArguments().getString(BundleConstant.ITEM))
        }
    }

    override fun providePresenter(): EditorLinkImagePresenter {
        return EditorLinkImagePresenter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            if (data != null && data.data != null) {
                val path = FileHelper.getPath(requireContext(), data.data!!)
                if (!InputHelper.isEmpty(path)) {
                    presenter?.onSubmit(InputHelper.toString(title), File(path!!))
                } else {
                    Toasty.error(App.getInstance(), getString(R.string.failed_selecting_image))
                        .show()
                }
            }
        }
    }

    @OnClick(R.id.select)
    fun onSelectClicked() {
        if (ActivityHelper.checkAndRequestReadWritePermission(requireActivity())) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.select_picture)),
                BundleConstant.REQUEST_CODE
            )
        }
    }

    @OnClick(R.id.cancel)
    fun onCancelClicked() {
        dismiss()
    }

    @OnClick(R.id.insert)
    fun onInsertClicked() {
        if (editorCallback != null) {
            editorCallback?.onAppendLink(
                InputHelper.toString(title),
                InputHelper.toString(link),
                isLink()
            )
        }
        dismiss()
    }

    private fun isLink(): Boolean {
        return arguments != null && requireArguments().getBoolean(BundleConstant.YES_NO_EXTRA)
    }

    companion object {
        fun newInstance(isLink: Boolean, link: String?): EditorLinkImageDialogFragment {
            val fragment = EditorLinkImageDialogFragment()
            fragment.arguments = Bundler
                .start()
                .put(BundleConstant.YES_NO_EXTRA, isLink)
                .put(BundleConstant.ITEM, link)
                .end()
            return fragment
        }
    }
}