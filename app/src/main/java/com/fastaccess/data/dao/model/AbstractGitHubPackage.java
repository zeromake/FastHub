package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.LongStream;
import com.annimon.stream.Stream;
import com.fastaccess.App;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.GithubFileModel;
import com.fastaccess.data.dao.converters.GitHubFilesConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.BlockingEntityStore;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by Kosh on 16 Mar 2017, 7:32 PM
 */

@Entity() public abstract class AbstractGitHubPackage implements Parcelable {
    @SerializedName("internal_id") @Key long id;
    @SerializedName("id") String packageId;
    @Convert(UserConverter.class) User owner;
    String name;
    String package_type;
    int version_count;
    String visibility;
    String url;
    String htmlUrl;
    Date created_at;
    Date updated_at;
    String description;

    public AbstractGitHubPackage() {}

    public static Disposable save(@NonNull List<GitHubPackage> models, @NonNull String ownerName) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                Login login = Login.getUser();
                if (login != null) {
                    if (login.getLogin().equalsIgnoreCase(ownerName)) {
                        BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                        dataSource.delete(GitHubPackage.class)
                                .where(GitHubPackage.OWNER_NAME.equal(ownerName))
                                .get()
                                .value();
                        if (!models.isEmpty()) {
                            for (GitHubPackage packageModel : models) {
                                dataSource.delete(GitHubPackage.class).where(GitHubPackage.ID.eq(packageModel.getId())).get().value();
                                packageModel.setOwnerName(ownerName);
                                dataSource.insert(packageModel);
                            }
                        }
                    } else {
                        App.getInstance().getDataStore().toBlocking()
                                .delete(GitHubPackage.class)
                                .where(GitHubPackage.OWNER_NAME.notEqual(ownerName)
                                        .or(GitHubPackage.OWNER_NAME.isNull()))
                                .get()
                                .value();
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    @NonNull public static Single<List<Gist>> getMyPackages(@NonNull String ownerName) {
        return App.getInstance()
                .getDataStore()
                .select(GitHubPackage.class)
                .where(GitHubPackage.OWNER_NAME.equal(ownerName))
                .get()
                .observable()
                .toList();
    }

    @NonNull public static Single<List<Gist>> getPackages() {
        return App.getInstance()
                .getDataStore()
                .select(GitHubPackage.class)
                .where(GitHubPackage.OWNER_NAME.isNull())
                .get()
                .observable()
                .toList();
    }

    public static Observable<Gist> getGist(@NonNull String gistId) {
        return App.getInstance()
                .getDataStore()
                .select(Gist.class)
                .where(Gist.GIST_ID.eq(gistId))
                .get()
                .observable();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractGitHubPackage that = (AbstractGitHubPackage) o;
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @NonNull public SpannableBuilder getDisplayTitle(boolean isFromProfile) {
        return getDisplayTitle(isFromProfile, false);
    }

    @NonNull public SpannableBuilder getDisplayTitle(boolean isFromProfile, boolean gistView) {
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        boolean addDescription = true;
        if (!isFromProfile) {
            if (owner != null) {
                spannableBuilder.bold(owner.getLogin());
            } else if (user != null) {
                spannableBuilder.bold(user.getLogin());
            } else {
                spannableBuilder.bold("Anonymous");
            }
            if (!gistView) {
                List<FilesListModel> files = getFilesAsList();
                if (!files.isEmpty()) {
                    FilesListModel filesListModel = files.get(0);
                    if (!InputHelper.isEmpty(filesListModel.getFilename()) && filesListModel.getFilename().trim().length() > 2) {
                        spannableBuilder.append(" ").append("/").append(" ")
                                .append(filesListModel.getFilename());
                        addDescription = false;
                    }
                }
            }
        }
        if (!InputHelper.isEmpty(description) && addDescription) {
            if (!InputHelper.isEmpty(spannableBuilder.toString())) {
                spannableBuilder.append(" ").append("/").append(" ");
            }
            spannableBuilder.append(description);
        }
        if (InputHelper.isEmpty(spannableBuilder.toString())) {
            if (isFromProfile) {
                List<FilesListModel> files = getFilesAsList();
                if (!files.isEmpty()) {
                    FilesListModel filesListModel = files.get(0);
                    if (!InputHelper.isEmpty(filesListModel.getFilename()) && filesListModel.getFilename().trim().length() > 2) {
                        spannableBuilder.append(" ")
                                .append(filesListModel.getFilename());
                    }
                }
            }
        }
        return spannableBuilder;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.packageId);
        dest.writeParcelable(this.owner, flags);
        dest.writeString(this.name);
        dest.writeString(this.package_type);
        dest.writeInt(this.version_count);
        dest.writeString(this.visibility);
        dest.writeString(this.url);
        dest.writeString(this.htmlUrl);
        dest.writeLong(this.created_at != null ? this.created_at.getTime() : -1);
        dest.writeLong(this.updated_at != null ? this.updated_at.getTime() : -1);
        dest.writeString(this.description);
    }

    protected AbstractGitHubPackage(Parcel in) {
        this.id = in.readLong();
        this.packageId = in.readString();
        this.owner = in.readParcelable(User.class.getClassLoader());
        this.name = in.readString();
        this.package_type = in.readString();
        this.version_count = in.readInt();
        this.visibility = in.readString();
        this.url = in.readString();
        this.htmlUrl = in.readString();
        long tmpCreatedAt = in.readLong();
        this.created_at = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updated_at = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.description = in.readString();
    }

    public static final Creator<GitHubPackage> CREATOR = new Creator<GitHubPackage>() {
        @Override public GitHubPackage createFromParcel(Parcel source) {return new GitHubPackage(source);}

        @Override public GitHubPackage[] newArray(int size) {return new GitHubPackage[size];}
    };
}
