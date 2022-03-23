package com.fastaccess.ui.modules.settings

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.annimon.stream.Stream
import com.fastaccess.R
import com.fastaccess.data.dao.AppLanguageModel
import com.fastaccess.helper.Logger
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.base.BaseBottomSheetDialog
import com.fastaccess.ui.widgets.FontTextView
import io.reactivex.functions.Action

/**
 * Created by JediB on 5/12/2017.
 */
class LanguageBottomSheetDialog : BaseBottomSheetDialog() {
    interface LanguageDialogListener {
        fun onLanguageChanged(action: Action?)
    }

    lateinit var title: FontTextView
    private lateinit var radioGroup: RadioGroup
    private var listener: LanguageDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LanguageDialogListener) {
            listener = context
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun layoutRes(): Int {
        return R.layout.picker_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.title = view.findViewById(R.id.title)
        this.radioGroup = view.findViewById(R.id.picker)
        val language = PrefGetter.getAppLanguage(resources)
        val values = resources.getStringArray(R.array.languages_array_values)
        val languageModels = Stream.of(*resources.getStringArray(R.array.languages_array))
            .mapIndexed { index: Int, s: String? ->
                AppLanguageModel(
                    values[index], s
                )
            }
            .sortBy { it.label!! }
            .toList()
        val padding = resources.getDimensionPixelSize(R.dimen.spacing_xs_large)
        for (i in languageModels.indices) {
            val radioButtonView = RadioButton(context)
            val params = RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButtonView.layoutParams = params
            val model = languageModels[i]
            radioButtonView.text = model.label
            radioButtonView.id = i
            radioButtonView.tag = model.value
            radioButtonView.gravity = Gravity.CENTER_VERTICAL
            radioButtonView.setPadding(padding, padding, padding, padding)
            radioGroup.addView(radioButtonView)
            if (model.value.equals(language, ignoreCase = true)) radioGroup.check(i)
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId: Int ->
            val tag = radioGroup.getChildAt(checkedId).tag as String
            Logger.e(tag)
            if (!tag.equals(language, ignoreCase = true)) {
                PrefGetter.setAppLangauge(tag)
                if (listener != null) listener!!.onLanguageChanged { dismiss() }
            }
        }
    }

    companion object {
        val TAG: String = LanguageBottomSheetDialog::class.java.simpleName
    }
}