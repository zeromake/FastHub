package com.fastaccess.ui.modules.repos.extras.milestone.create

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.OnTouch
import com.fastaccess.R
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.datetimepicker.DatePickerFragmentDialog
import com.fastaccess.datetimepicker.callback.DatePickerCallback
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.toString
import com.fastaccess.helper.ParseDateFormat.Companion.prettifyDate
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.modules.repos.extras.milestone.create.CreateMilestoneDialogFragment
import com.fastaccess.ui.modules.repos.extras.milestone.create.CreateMilestoneMvp.OnMilestoneAdded
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Kosh on 04 Mar 2017, 10:40 PM
 */
class CreateMilestoneDialogFragment :
    BaseDialogFragment<CreateMilestoneMvp.View, CreateMilestonePresenter>(),
    CreateMilestoneMvp.View, DatePickerCallback {
    @JvmField
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.title)
    var title: TextInputLayout? = null

    @JvmField
    @BindView(R.id.dueOnEditText)
    var dueOnEditText: TextInputEditText? = null

    @JvmField
    @BindView(R.id.description)
    var description: TextInputLayout? = null
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

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val login = arguments!!.getString(BundleConstant.EXTRA)
        val repo = arguments!!.getString(BundleConstant.ID)
        if (login == null || repo == null) {
            return
        }
        toolbar!!.setTitle(R.string.create_milestone)
        toolbar!!.setNavigationIcon(R.drawable.ic_clear)
        toolbar!!.setNavigationOnClickListener { item: View? -> dismiss() }
        toolbar!!.inflateMenu(R.menu.add_menu)
        toolbar!!.menu.findItem(R.id.add).setIcon(R.drawable.ic_send)
        toolbar!!.setOnMenuItemClickListener { item: MenuItem? ->
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

    @OnTouch(R.id.dueOnEditText)
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
        val TAG = CreateMilestoneDialogFragment::class.java.simpleName
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