package com.fastaccess.ui.modules.repos.git.delete

import android.content.Context
import android.os.Bundle
import android.view.View
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.ui.base.BaseBottomSheetDialog
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Hashemsergani on 02/09/2017.
 */
class DeleteFileBottomSheetFragment : BaseBottomSheetDialog() {
    val description: TextInputLayout by viewFind(R.id.description)
    val fileName: TextInputLayout by viewFind(R.id.fileName)

    private var deleteCallback: DeleteContentFileCallback? = null


    fun onDeleteClicked() {
        description.error =
            if (InputHelper.isEmpty(description)) getString(R.string.required_field) else null
        if (!InputHelper.isEmpty(description)) {
            val position = arguments?.getInt(BundleConstant.EXTRA)
            position?.let {
                deleteCallback?.onDelete(InputHelper.toString(description), position)
            }
            dismiss()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is DeleteContentFileCallback) {
            deleteCallback = parentFragment as DeleteContentFileCallback
        } else if (context is DeleteContentFileCallback) {
            deleteCallback = context
        }
    }

    override fun onDetach() {
        deleteCallback = null
        super.onDetach()
    }

    override fun layoutRes(): Int = R.layout.delete_repo_file_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.delete).setOnThrottleClickListener {
            onDeleteClicked()
        }
        view.findViewById<View>(R.id.cancel).setOnThrottleClickListener {
            dismiss()
        }
        fileName.isEnabled = false
        fileName.editText?.setText(arguments?.getString(BundleConstant.ITEM))
    }

    companion object {
        fun newInstance(position: Int, path: String): DeleteFileBottomSheetFragment {
            val fragment = DeleteFileBottomSheetFragment()
            fragment.arguments = Bundler.start()
                .put(BundleConstant.EXTRA, position)
                .put(BundleConstant.ITEM, path)
                .end()
            return fragment
        }
    }
}