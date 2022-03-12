package com.fastaccess.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.ViewGroup;

import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.ui.adapter.viewholder.PinnedReposViewHolder;
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter;
import com.fastaccess.ui.base.adapter.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class PinnedReposAdapter extends BaseRecyclerAdapter<PinnedRepos, PinnedReposViewHolder, BaseViewHolder.OnItemClickListener<PinnedRepos>> {

    private boolean singleLine;

    public PinnedReposAdapter(boolean singleLine) {
        super();
        this.singleLine = singleLine;
    }

    public PinnedReposAdapter(@NonNull List<PinnedRepos> data, @Nullable BaseViewHolder.OnItemClickListener<PinnedRepos> listener) {
        super(data, listener);
    }

    @Override
    protected PinnedReposViewHolder viewHolder(ViewGroup parent, int viewType) {
        return PinnedReposViewHolder.newInstance(parent, this, singleLine);
    }

    @Override
    protected void onBindView(PinnedReposViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
