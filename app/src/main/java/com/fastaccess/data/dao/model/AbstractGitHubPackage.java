package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.fastaccess.App;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.helper.RxHelper;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.BlockingEntityStore;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by Kosh on 16 Mar 2017, 7:32 PM
 */

@Entity() public abstract class AbstractGitHubPackage implements Parcelable {
    @Key long id;
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

    public Single<GitHubPackage> save(GitHubPackage entity) {
        return RxHelper.getSingle(App.getInstance().getDataStore().upsert(entity));
    }

    public static Disposable save(@NonNull List<GitHubPackage> models, @NonNull long id) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                dataSource.delete(GitHubPackage.class)
                        .where(GitHubPackage.ID.equal(id))
                        .get()
                        .value();
                if (!models.isEmpty()) {
                    for (GitHubPackage packageModel : models) {
                        dataSource.delete(GitHubPackage.class).where(GitHubPackage.ID.eq(packageModel.getId())).get().value();
                        dataSource.insert(packageModel);
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    @NonNull public static Single<List<GitHubPackage>> getPackagesOf(@NonNull String ownerName, @NonNull String package_type) {
        return App.getInstance()
                .getDataStore()
                .select(GitHubPackage.class)
                .get()
                .observable()
                .filter(it -> it.owner.login.equals(ownerName) && it.package_type.equals(package_type))
                .toList();
    }

    public AbstractGitHubPackage() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
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
