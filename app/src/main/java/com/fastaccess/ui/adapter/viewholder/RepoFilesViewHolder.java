package com.fastaccess.ui.adapter.viewholder;

import androidx.annotation.NonNull;

import android.content.res.Resources;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.data.dao.types.FilesType;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;


/**
 * Created by Kosh on 15 Feb 2017, 10:29 PM
 */

public class RepoFilesViewHolder extends BaseViewHolder<RepoFile> {

    ForegroundImageView contentTypeImage;
    FontTextView title;
    FontTextView size;
    ForegroundImageView menu;
    String file;

    @Override public void onClick(View v) {
        if (v.getId() == R.id.contentTypeImage) {
            itemView.callOnClick();
        } else {
            super.onClick(v);
        }
    }

    private RepoFilesViewHolder(@NonNull View itemView, @NonNull BaseRecyclerAdapter<RepoFile, RepoFilesViewHolder, BaseViewHolder
            .OnItemClickListener<RepoFile>> adapter) {
        super(itemView, adapter);
        this.contentTypeImage = itemView.findViewById((R.id.contentTypeImage));
        this.title = itemView.findViewById((R.id.title));
        this.size = itemView.findViewById((R.id.size));
        this.menu = itemView.findViewById((R.id.menu));
        Resources $res = itemView.getContext().getResources();
        this.file = $res.getString(R.string.file);

        menu.setOnClickListener(this);
        contentTypeImage.setOnClickListener(this);
    }

    public static RepoFilesViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter<RepoFile, RepoFilesViewHolder, BaseViewHolder
            .OnItemClickListener<RepoFile>> adapter) {
        return new RepoFilesViewHolder(getView(viewGroup, R.layout.repo_files_row_item), adapter);
    }

    @Override public void bind(@NonNull RepoFile filesModel) {
        contentTypeImage.setContentDescription(String.format("%s %s", filesModel.getName(), file));
        title.setText(filesModel.getName());
        if (filesModel.getType() != null && filesModel.getType().getIcon() != 0) {
            contentTypeImage.setImageResource(filesModel.getType().getIcon());
            if (filesModel.getType() == FilesType.file) {
                size.setText(Formatter.formatFileSize(size.getContext(), filesModel.getSize()));
                size.setVisibility(View.VISIBLE);
            } else {
                size.setVisibility(View.GONE);
            }
        }
    }
}
