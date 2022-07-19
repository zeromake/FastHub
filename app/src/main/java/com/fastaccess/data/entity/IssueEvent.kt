package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.data.dao.RenameModel
import com.fastaccess.data.dao.TeamsModel
import com.fastaccess.data.dao.types.IssueEventType
import com.fastaccess.data.entity.converters.*
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.readEnum
import com.fastaccess.helper.writeAtCompat
import com.fastaccess.helper.writeEnum
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class IssueEvent(
    @Id(assignable = true)
    var id: Long = 0,
    var url: String? = null,
    @Convert(converter = IssueEventTypeConverter::class, dbType = Int::class)
    var event: IssueEventType? = null,
    @Convert(converter = UserConverter::class, dbType = String::class)
    var actor: User? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var assigner: User? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var assignee: User? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var requestedReviewer: User? = null,

    @Convert(converter = TeamConverter::class, dbType = String::class)
    var requestedTeam: TeamsModel? = null,

    @Convert(converter = MilestoneConverter::class, dbType = String::class)
    var milestone: MilestoneModel? = null,

    @Convert(converter = RenameConverter::class, dbType = String::class)
    var rename: RenameModel? = null,

    @Convert(converter = IssueConverter::class, dbType = String::class)
    var source: Issue? = null,

    @Convert(converter = LabelConverter::class, dbType = String::class)
    var label: LabelModel? = null,
    var commitId: String? = null,
    var commitUrl: String? = null,
    var createdAt: Date? = null,
    var issueId: String? = null,
    var repoId: String? = null,
    var login: String? = null,

    @Transient
    var labels: List<LabelModel>? = null,

    @Transient
    var issue: Issue? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readEnum(IssueEventType.values()),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(TeamsModel::class.java.classLoader),
        parcel.readParcelable(MilestoneModel::class.java.classLoader),
        parcel.readParcelable(RenameModel::class.java.classLoader),
        parcel.readParcelable(Issue::class.java.classLoader),
        parcel.readParcelable(LabelModel::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readAtCompat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(LabelModel.CREATOR),
        parcel.readParcelable(Issue::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(url)
        parcel.writeEnum(event)
        parcel.writeParcelable(actor, flags)
        parcel.writeParcelable(assigner, flags)
        parcel.writeParcelable(assignee, flags)
        parcel.writeParcelable(requestedReviewer, flags)
        parcel.writeParcelable(requestedTeam, flags)
        parcel.writeParcelable(milestone, flags)
        parcel.writeParcelable(rename, flags)
        parcel.writeParcelable(source, flags)
        parcel.writeParcelable(label, flags)
        parcel.writeString(commitId)
        parcel.writeString(commitUrl)
        parcel.writeAtCompat(createdAt)
        parcel.writeString(issueId)
        parcel.writeString(repoId)
        parcel.writeString(login)
        parcel.writeTypedList(labels)
        parcel.writeParcelable(issue, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IssueEvent> {
        override fun createFromParcel(parcel: Parcel): IssueEvent {
            return IssueEvent(parcel)
        }

        override fun newArray(size: Int): Array<IssueEvent?> {
            return arrayOfNulls(size)
        }
    }
}
