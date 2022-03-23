package com.fastaccess.ui.adapter.viewholder

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.fastaccess.R
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.helper.ViewHelper.getPatchAdditionColor
import com.fastaccess.helper.ViewHelper.getPatchDeletionColor
import com.fastaccess.helper.ViewHelper.getPatchRefColor
import com.fastaccess.ui.adapter.CommitLinesAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder

/**
 * Created by Kosh on 31 Dec 2016, 3:12 PM
 */
class CommitLinesViewHolder private constructor(
    itemView: View,
    adapter: CommitLinesAdapter?
) : BaseViewHolder<CommitLinesModel>(itemView, adapter) {
    val textView: AppCompatTextView? = itemView.findViewById(R.id.textView)
    val leftLinNo: AppCompatTextView? = itemView.findViewById(R.id.leftLinNo)
    val rightLinNo: AppCompatTextView? = itemView.findViewById(R.id.rightLinNo)
    val hasComment: View? = itemView.findViewById(R.id.hasComment)
    private val patchAdditionColor: Int = getPatchAdditionColor(itemView.context)
    private val patchDeletionColor: Int = getPatchDeletionColor(itemView.context)
    private val patchRefColor: Int = getPatchRefColor(itemView.context)
    override fun bind(t: CommitLinesModel) {
        leftLinNo!!.text = if (t.leftLineNo > 0) t.leftLineNo.toString() else "  "
        rightLinNo!!.text = if (t.rightLineNo > 0) t.rightLineNo.toString() else "  "
        hasComment!!.visibility =
            if (t.isHasCommentedOn) View.VISIBLE else View.GONE
        when (t.color) {
            CommitLinesModel.ADDITION -> textView!!.setBackgroundColor(patchAdditionColor)
            CommitLinesModel.DELETION -> textView!!.setBackgroundColor(patchDeletionColor)
            CommitLinesModel.PATCH -> {
                leftLinNo.visibility = View.GONE
                rightLinNo.visibility = View.GONE
                textView!!.setBackgroundColor(patchRefColor)
            }
            else -> textView!!.setBackgroundColor(Color.TRANSPARENT)
        }
        if (t.isNoNewLine) {
            textView.text = builder().append(t.text).append(" ")
                .append(ContextCompat.getDrawable(textView.context, R.drawable.ic_newline))
        } else {
            textView.text = t.text
        }
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(
            viewGroup: ViewGroup,
            adapter: CommitLinesAdapter?
        ): CommitLinesViewHolder {
            return CommitLinesViewHolder(getView(viewGroup, R.layout.commit_line_row_item), adapter)
        }
    }

    init {
        textView!!.setOnLongClickListener(this)
    }
}