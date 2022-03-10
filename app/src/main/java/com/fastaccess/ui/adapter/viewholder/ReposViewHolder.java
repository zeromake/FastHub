package com.fastaccess.ui.adapter.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.colors.ColorsProvider;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.text.NumberFormat;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class ReposViewHolder extends BaseViewHolder<Repo> {

    FontTextView title;
    FontTextView date;
    FontTextView stars;
    FontTextView forks;
    FontTextView language;
    FontTextView size;
    @Nullable AvatarLayout avatarLayout;
    String forked;
    String privateRepo;
    int forkColor;
    int privateColor;
    private final boolean isStarred;
    private final boolean withImage;

    private ReposViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter<Repo, ReposViewHolder, BaseViewHolder.OnItemClickListener<Repo>> adapter, boolean isStarred, boolean withImage) {
        super(itemView, adapter);
        Context context = itemView.getContext();
        Resources res = context.getResources();
        this.title = itemView.findViewById((R.id.title));
        this.date = itemView.findViewById((R.id.date));
        this.stars = itemView.findViewById((R.id.stars));
        this.forks = itemView.findViewById((R.id.forks));
        this.language = itemView.findViewById((R.id.language));
        this.size = itemView.findViewById((R.id.size));
        this.avatarLayout = itemView.findViewById((R.id.avatarLayout));
        this.forkColor = ContextCompat.getColor(context, R.color.material_indigo_700);
        this.privateColor = ContextCompat.getColor(context, R.color.material_grey_700);
        this.forked = res.getString(R.string.forked);
        this.privateRepo = res.getString(R.string.private_repo);
        this.isStarred = isStarred;
        this.withImage = withImage;
    }

    public static ReposViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter<Repo, ReposViewHolder, BaseViewHolder.OnItemClickListener<Repo>> adapter, boolean isStarred, boolean withImage) {
        if (withImage) {
            return new ReposViewHolder(getView(viewGroup, R.layout.repos_row_item), adapter, isStarred, true);
        } else {
            return new ReposViewHolder(getView(viewGroup, R.layout.repos_row_no_image_item), adapter, isStarred, false);
        }

    }

    @Override public void bind(@NonNull Repo repo) {
        if (repo.isFork() && !isStarred) {
            title.setText(SpannableBuilder.builder()
                    .append(" " + forked + " ", new LabelSpan(forkColor))
                    .append(" ")
                    .append(repo.getName(), new LabelSpan(Color.TRANSPARENT)));
        } else if (repo.isPrivateX()) {
            title.setText(SpannableBuilder.builder()
                    .append(" " + privateRepo + " ", new LabelSpan(privateColor))
                    .append(" ")
                    .append(repo.getName(), new LabelSpan(Color.TRANSPARENT)));
        } else {
            title.setText(!isStarred ? repo.getName() : repo.getFullName());
        }
        if (withImage) {
            String avatar = repo.getOwner() != null ? repo.getOwner().getAvatarUrl() : null;
            String login = repo.getOwner() != null ? repo.getOwner().getLogin() : null;
            boolean isOrg = repo.getOwner() != null && repo.getOwner().isOrganizationType();
            if (avatarLayout != null) {
                avatarLayout.setVisibility(View.VISIBLE);
                avatarLayout.setUrl(avatar, login, isOrg, LinkParserHelper.isEnterprise(repo.getHtmlUrl()));
            }
        }
        long repoSize = repo.getSize() > 0 ? (repo.getSize() * 1000) : repo.getSize();
        size.setText(Formatter.formatFileSize(size.getContext(), repoSize));
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        stars.setText(numberFormat.format(repo.getStargazersCount()));
        forks.setText(numberFormat.format(repo.getForks()));
        date.setText(ParseDateFormat.getTimeAgo(repo.getUpdatedAt()));
        if (!InputHelper.isEmpty(repo.getLanguage())) {
            language.setText(repo.getLanguage());
            language.setTextColor(ColorsProvider.getColorAsColor(repo.getLanguage(), language.getContext()));
            language.setVisibility(View.VISIBLE);
        } else {
            language.setTextColor(Color.BLACK);
            language.setVisibility(View.GONE);
            language.setText("");
        }
    }
}
