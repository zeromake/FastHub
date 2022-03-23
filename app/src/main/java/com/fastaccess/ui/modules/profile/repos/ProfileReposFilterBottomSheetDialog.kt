package com.fastaccess.ui.modules.profile.repos

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.fastaccess.R
import com.fastaccess.data.dao.FilterOptionsModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.ui.base.BaseBottomSheetDialog
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.profile.org.repos.OrgReposFragment
import com.fastaccess.ui.modules.profile.org.repos.OrgReposMvp
import com.fastaccess.ui.modules.search.SearchUserActivity.Companion.getIntent
import com.fastaccess.utils.setOnThrottleClickListener

class ProfileReposFilterBottomSheetDialog : BaseBottomSheetDialog() {
    val typeSelectionSpinner: Spinner? by viewFind(R.id.type_selection)
    val sortSelectionSpinner: Spinner? by viewFind(R.id.sort_selection)

    //    val applyBtn: View? by viewFind(R.id.filter_sheet_apply_btn)
    val sortDirectionSpinner: Spinner? by viewFind(R.id.sort_direction_selection)
    val sortLayout: LinearLayout? by viewFind(R.id.sort_layout)
    val sortDirectionLayout: LinearLayout? by viewFind(R.id.sort_direction_layout)

    private var currentFilterOptions: FilterOptionsModel? = null
    private var listener: ProfileReposFilterChangeListener? = null
    override fun layoutRes(): Int {
        return R.layout.filter_bottom_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.filter_sheet_apply_btn).setOnThrottleClickListener {
            onApply()
        }
        view.findViewById<View>(R.id.filter_sheet_reset_btn).setOnThrottleClickListener {
            onReset()
        }
        view.findViewById<View>(R.id.filter_sheet_search_btn).setOnThrottleClickListener {
            startSearch()
        }

        currentFilterOptions = requireArguments().getParcelable(BundleConstant.ITEM)
        if (currentFilterOptions == null) return
        val typesAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1,
            currentFilterOptions!!.typesList
        )
        val sortOptionsAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1,
            currentFilterOptions!!.sortOptionList
        )
        val sortDirectionAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1,
            currentFilterOptions!!.sortDirectionList
        )
        typeSelectionSpinner!!.adapter = typesAdapter
        sortSelectionSpinner!!.adapter = sortOptionsAdapter
        sortDirectionSpinner!!.adapter = sortDirectionAdapter
        typeSelectionSpinner!!.setSelection(currentFilterOptions!!.selectedTypeIndex)
        sortSelectionSpinner!!.setSelection(currentFilterOptions!!.selectedSortOptionIndex)
        sortDirectionSpinner!!.setSelection(currentFilterOptions!!.selectedSortDirectionIndex)
        if (currentFilterOptions!!.isOrg) {
            sortLayout!!.visibility = View.GONE
            sortDirectionLayout!!.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener =
            if (context is OrgReposMvp.View || parentFragment is OrgReposMvp.View) {
                parentFragment as OrgReposFragment?
            } else {
                parentFragment as ProfileReposFragment?
            }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun onApply() {
        if (listener != null) {
            listener!!.onTypeSelected(typeSelectionSpinner!!.selectedItem as String)
            listener!!.onSortOptionSelected(sortSelectionSpinner!!.selectedItem as String)
            listener!!.onSortDirectionSelected(sortDirectionSpinner!!.selectedItem as String)
            listener!!.onFilterApply()
            dismiss()
        }
    }

    fun onReset() {
        typeSelectionSpinner!!.setSelection(0)
        sortDirectionSpinner!!.setSelection(0)
        sortSelectionSpinner!!.setSelection(0)
    }

    fun startSearch() {
        if (listener != null) {
            val intent = getIntent(requireContext(), listener!!.login!!, "")
            startActivity(intent)
        }
        dismiss()
    }

    interface ProfileReposFilterChangeListener {
        fun onFilterApply()
        fun onTypeSelected(selectedType: String?)
        fun onSortOptionSelected(selectedSortOption: String?)
        fun onSortDirectionSelected(selectedSortDirection: String?)
        val login: String?
    }

    companion object {
        fun newInstance(currentFilterOptions: FilterOptionsModel): ProfileReposFilterBottomSheetDialog {
            val fragment = ProfileReposFilterBottomSheetDialog()
            fragment.arguments =
                start().put(BundleConstant.ITEM, currentFilterOptions).end()
            return fragment
        }
    }
}