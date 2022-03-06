package com.fastaccess.ui.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.TeamsModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.TeamsAdapter;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class TeamsViewHolder extends BaseViewHolder<TeamsModel> {

    FontTextView title;
    FontTextView date;

    private TeamsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter<?, ?, ?> adapter) {
        super(itemView, adapter);
        this.title = itemView.findViewById((R.id.title));
        this.date = itemView.findViewById((R.id.date));
    }

    public static TeamsViewHolder newInstance(@NonNull ViewGroup viewGroup, @NonNull BaseRecyclerAdapter<?, ?, ?> adapter) {
        return new TeamsViewHolder(getView(viewGroup, R.layout.feeds_row_no_image_item), adapter);
    }

    @Override public void bind(@NonNull TeamsModel user) {
        title.setText(!InputHelper.isEmpty(user.getName()) ? user.getName() : user.getSlug());
        if (!InputHelper.isEmpty(user.getDescription())) {
            date.setText(user.getDescription());
        } else {
            date.setText(InputHelper.toNA(user.getSlug()));
        }
    }
}
