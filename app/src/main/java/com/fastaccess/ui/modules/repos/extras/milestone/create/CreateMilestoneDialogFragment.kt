package com.fastaccess.ui.modules.repos.extras.milestone.create

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.fastaccess.R
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.datetimepicker.DatePickerFragmentDialog
import com.fastaccess.datetimepicker.callback.DatePickerCallback
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.toString
import com.fastaccess.helper.ParseDateFormat.Companion.prettifyDate
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.extras.milestone.create.CreateMilestoneMvp.OnMilestoneAdded
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Kosh on 04 Mar 2017, 10:40 PM
 */
class CreateMilestoneDialogFragment :
    BaseDialogFragment<CreateMilestoneMvp.View, CreateMilestonePresenter>(),
    CreateMilestoneMvp.View, DatePickerCallback {
    val toolbar: Toolbar? by viewFind(R.id.toolbar)
    val title: TextInputLayout? by viewFind(R.id.title)
    val dueOnEditText: TextInputEditText? by viewFind(R.id.dueOnEditText)
    val description: TextInputLayout? by viewFind(R.id.description)
    private var onMilestoneAdded: OnMilestoneAdded? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onMilestoneAdded = if (parentFragment is OnMilestoneAdded) {
            parentFragment as OnMilestoneAdded?
        } else {
            context as OnMilestoneAdded
        }
    }

    override fun onDetach() {
        onMilestoneAdded = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int {
        return R.layout.create_milestone_layout
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val login = requireArguments().getString(BundleConstant.EXTRA)
        val repo = requireArguments().getString(BundleConstant.ID)
        if (login == null || repo == null) {
            return
        }
        dueOnEditText!!.setOnTouchListener { _, event ->
            onTouch(event)
        }
        toolbar!!.setTitle(R.string.create_milestone)
        toolbar!!.setNavigationIcon(R.drawable.ic_clear)
        toolbar!!.setNavigationOnClickListener { dismiss() }
        toolbar!!.inflateMenu(R.menu.add_menu)
        toolbar!!.menu.findItem(R.id.add).setIcon(R.drawable.ic_send)
        toolbar!!.setOnMenuItemClickListener {
            presenter!!.onSubmit(
                toString(title),
                toString(dueOnEditText),
                toString(description),
                login,
                repo
            )
            true
        }
    }

    override fun providePresenter(): CreateMilestonePresenter {
        return CreateMilestonePresenter()
    }

    fun onTouch(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            DatePickerFragmentDialog.newInstance()
                .show(childFragmentManager, "DatePickerFragmentDialog")
        }
        return false
    }

    override fun onDateSet(date: Long) {
        if (date > 0) {
            dueOnEditText!!.setText(prettifyDate(date))
        }
    }

    override fun onShowTitleError(isError: Boolean) {
        title!!.error = if (isError) getString(R.string.required_field) else null
    }

    override fun onMilestoneAdded(milestoneModel: MilestoneModel) {
        hideProgress()
        onMilestoneAdded!!.onMilestoneAdded(milestoneModel)
        dismiss()
    }

    companion object {
        val TAG: String = CreateMilestoneDialogFragment::class.java.simpleName
        fun newInstance(login: String, repo: String): CreateMilestoneDialogFragment {
            val fragment = CreateMilestoneDialogFragment()
            fragment.arguments = start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end()
            return fragment
        }
    }
}