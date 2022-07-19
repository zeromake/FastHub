package com.fastaccess.ui.modules.editor.popup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.FileHelper
import com.fastaccess.helper.InputHelper
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp.EditorLinkCallback
import com.fastaccess.ui.widgets.FontButton
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.textfield.TextInputLayout
import es.dmoral.toasty.Toasty
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
        this.title = view.findViewById(R.id.title)
        this.link = view.findViewById(R.id.link)
        this.select = view.findViewById(R.id.select)
        this.select.setOnThrottleClickListener {
            onSelectClicked()
        }
        view.findViewById<View>(R.id.cancel).setOnThrottleClickListener {
            onCancelClicked()
        }
        view.findViewById<View>(R.id.insert).setOnThrottleClickListener {
            onInsertClicked()
        }
        select.visibility = if (isLink()) View.GONE else View.VISIBLE
        if (savedInstanceState == null) {
            title.editText!!.setText(requireArguments().getString(BundleConstant.ITEM))
        }
    }

    override fun providePresenter(): EditorLinkImagePresenter {
        return EditorLinkImagePresenter()
    }

    val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val data = it.data
        if (it.resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val path = FileHelper.getPath(requireContext(), data.data!!)
            if (!InputHelper.isEmpty(path)) {
                presenter?.onSubmit(InputHelper.toString(title), File(path!!))
            } else {
                Toasty.error(
                    requireContext(),
                    getString(R.string.failed_selecting_image)
                )
                    .show()
            }
        }
    }

    private fun onSelectClicked() {
//        if (ActivityHelper.checkAndRequestReadWritePermission(requireActivity())) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        launcher.launch(Intent.createChooser(intent, getString(R.string.select_picture)))
//        }
    }

    private fun onCancelClicked() {
        dismiss()
    }

    private fun onInsertClicked() {
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