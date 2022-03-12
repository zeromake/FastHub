package com.fastaccess.ui.adapter.viewholder;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter;
import com.fastaccess.ui.base.adapter.BaseViewHolder;

/**
 * Created by Kosh on 18 Feb 2017, 2:53 AM
 */

public class RepoFilePathsViewHolder extends BaseViewHolder<RepoFile> {

    FontTextView pathName;

    private RepoFilePathsViewHolder(@NonNull View itemView, @NonNull BaseRecyclerAdapter<RepoFile, RepoFilePathsViewHolder, BaseViewHolder
            .OnItemClickListener<RepoFile>> baseAdapter) {
        super(itemView, baseAdapter);
        this.pathName = itemView.findViewById(R.id.pathName);
    }

    public static RepoFilePathsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter<RepoFile, RepoFilePathsViewHolder, BaseViewHolder
            .OnItemClickListener<RepoFile>> adapter) {
        return new RepoFilePathsViewHolder(getView(viewGroup, R.layout.file_path_row_item), adapter);
    }

    @Override public void bind(@NonNull RepoFile filesModel) {
        pathName.setText(filesModel.getName());
    }
}
