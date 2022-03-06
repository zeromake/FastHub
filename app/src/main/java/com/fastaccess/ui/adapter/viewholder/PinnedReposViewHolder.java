package com.fastaccess.ui.adapter.viewholder;

import android.content.Context;
import android.content.res.Resources;
import androidx.core.content.ContextCompat;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.PinnedRepos;
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

public class PinnedReposViewHolder extends BaseViewHolder<PinnedRepos> {

    FontTextView title;
    @Nullable AvatarLayout avatarLayout;
    @Nullable FontTextView date;
    @Nullable FontTextView stars;
    @Nullable FontTextView forks;
    @Nullable FontTextView language;
    String forked;
    String privateRepo;
    int forkColor;
    int privateColor;

    private PinnedReposViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        this.title = itemView.findViewById(R.id.title);
        this.avatarLayout = itemView.findViewById(R.id.avatarLayout);
        this.date = itemView.findViewById(R.id.date);
        this.stars = itemView.findViewById(R.id.stars);
        this.forks = itemView.findViewById(R.id.forks);
        this.language = itemView.findViewById(R.id.language);

        Context $$context = itemView.getContext();
        Resources $$res = $$context.getResources();

        this.forked = $$res.getString(R.string.forked);
        this.privateRepo = $$res.getString(R.string.private_repo);
        this.forkColor = ContextCompat.getColor($$context, R.color.material_indigo_700);
        this.privateColor = ContextCompat.getColor($$context, R.color.material_grey_700);
    }

    public static PinnedReposViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter, boolean singleLine) {
        return new PinnedReposViewHolder(getView(viewGroup,
                singleLine ? R.layout.repos_row_item_menu : R.layout.repos_row_item), adapter);
    }

    @Override public void bind(@NonNull PinnedRepos pinnedRepos) {
        Repo repo = pinnedRepos.getPinnedRepo();
        if (repo == null) return;
        if (repo.isFork()) {
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
            title.setText(repo.getFullName());
        }
        String avatar = repo.getOwner() != null ? repo.getOwner().getAvatarUrl() : null;
        String login = repo.getOwner() != null ? repo.getOwner().getLogin() : null;
        boolean isOrg = repo.getOwner() != null && repo.getOwner().isOrganizationType();
        if (avatarLayout != null) {
            avatarLayout.setVisibility(View.VISIBLE);
            avatarLayout.setUrl(avatar, login, isOrg, LinkParserHelper.isEnterprise(repo.getHtmlUrl()));
        }
        if (stars != null && forks != null && date != null && language != null) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            stars.setText(numberFormat.format(repo.getStargazersCount()));
            forks.setText(numberFormat.format(repo.getForks()));
            date.setText(ParseDateFormat.getTimeAgo(repo.getUpdatedAt()));
            if (!InputHelper.isEmpty(repo.getLanguage())) {
                language.setText(repo.getLanguage());
                language.setTextColor(ColorsProvider.getColorAsColor(repo.getLanguage(), language.getContext()));
                language.setVisibility(View.VISIBLE);
            }
        }
    }
}
