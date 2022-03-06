package com.fastaccess.ui.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.SearchCodeModel;
import com.fastaccess.ui.adapter.SearchCodeAdapter;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;


/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class SearchCodeViewHolder extends BaseViewHolder<SearchCodeModel> {

    FontTextView title;
    FontTextView details;
    View commentsNo;

    private SearchCodeViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter<?, ?, ?> adapter) {
        super(itemView, adapter);
        this.title = itemView.findViewById(R.id.title);
        this.details = itemView.findViewById(R.id.details);
        this.commentsNo = itemView.findViewById(R.id.commentsNo);
    }

    public static SearchCodeViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter<?, ?, ?> adapter) {
        return new SearchCodeViewHolder(getView(viewGroup, R.layout.issue_no_image_row_item), adapter);
    }

    public void bind(@NonNull SearchCodeModel codeModel, boolean showRepoName) {
        if(showRepoName) {
            title.setText(codeModel.getRepository() != null ? codeModel.getRepository().getFullName() : "N/A");
            details.setText(codeModel.getName());
        } else {
            title.setText(codeModel.getName());
            details.setText(codeModel.getPath());
        }
        commentsNo.setVisibility(View.GONE);
    }

    @Override public void bind(@NonNull SearchCodeModel searchCodeModel) {}
}