package com.fastaccess.ui.modules.repos.extras.labels.create

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.InputHelper.toString
import com.fastaccess.ui.adapter.LabelColorsAdapter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.modules.repos.extras.labels.LabelsMvp
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Kosh on 02 Apr 2017, 5:38 PM
 */
class CreateLabelDialogFragment : BaseDialogFragment<CreateLabelMvp.View, CreateLabelPresenter>(),
    CreateLabelMvp.View {
    @JvmField
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.name)
    var name: TextInputLayout? = null

    @JvmField
    @BindView(R.id.description)
    var description: TextInputLayout? = null

    @JvmField
    @BindView(R.id.recycler)
    var recycler: DynamicRecyclerView? = null

    @JvmField
    @BindView(R.id.fastScroller)
    var fastScroller: RecyclerViewFastScroller? = null
    private var viewListener: LabelsMvp.View? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is LabelsMvp.View) {
            viewListener = parentFragment as LabelsMvp.View?
        } else if (context is LabelsMvp.View) {
            viewListener = context
        }
    }

    override fun onDetach() {
        viewListener = null
        super.onDetach()
    }

    override fun onSuccessfullyCreated(labelModel1: LabelModel) {
        hideProgress()
        if (viewListener != null) viewListener!!.onLabelAdded(labelModel1)
        dismiss()
    }

    override fun onColorSelected(color: String) {
        description!!.editText!!.setText(color.replaceFirst("#".toRegex(), ""))
    }

    override fun fragmentLayout(): Int {
        return R.layout.create_label_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val login = requireArguments().getString(BundleConstant.EXTRA)
        val repo = requireArguments().getString(BundleConstant.ID)
        if (login == null || repo == null) {
            return
        }
        recycler!!.adapter = LabelColorsAdapter(
            resources.getStringArray(R.array.label_colors).toMutableList(),
            presenter
        )
        recycler!!.addKeyLineDivider()
        toolbar!!.setTitle(R.string.create_label)
        toolbar!!.setNavigationIcon(R.drawable.ic_clear)
        toolbar!!.setNavigationOnClickListener { dismiss() }
        toolbar!!.inflateMenu(R.menu.add_menu)
        toolbar!!.menu.findItem(R.id.add).setIcon(R.drawable.ic_send)
        toolbar!!.setOnMenuItemClickListener {
            val emptyColor = isEmpty(description)
            val emptyName = isEmpty(name)
            description!!.error = if (emptyColor) getString(R.string.required_field) else null
            name!!.error = if (emptyName) getString(R.string.required_field) else null
            if (!emptyColor && !emptyName) {
                presenter!!.onSubmitLabel(toString(name), toString(description), repo, login)
            }
            true
        }
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun providePresenter(): CreateLabelPresenter {
        return CreateLabelPresenter()
    }

    companion object {
        @JvmStatic
        fun newInstance(login: String, repo: String): CreateLabelDialogFragment {
            val fragment = CreateLabelDialogFragment()
            fragment.arguments = start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end()
            return fragment
        }
    }
}