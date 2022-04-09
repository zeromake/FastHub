package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.PayloadModel
import com.fastaccess.data.dao.types.EventsType
import com.fastaccess.data.entity.converters.EventsTypeConverter
import com.fastaccess.data.entity.converters.PayloadConverter
import com.fastaccess.data.entity.converters.RepoConverter
import com.fastaccess.data.entity.converters.UserConverter
import com.fastaccess.helper.*
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class Event(
    @Id(assignable = true)
    var id: Long = 0,
    @Convert(converter = EventsTypeConverter::class, dbType = Int::class)
    var type: EventsType? = null,
    var createdAt: Date? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var actor: User? = null,

    @Convert(converter = RepoConverter::class, dbType = String::class)
    var repo: Repo? = null,

    @Convert(converter = PayloadConverter::class, dbType = String::class)
    var payload: PayloadModel? = null,

    @SerializedName("public")
    var publicEvent: Boolean = false,

    var login: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readEnum(EventsType.values()),
        parcel.readAtCompat(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(Repo::class.java.classLoader),
        parcel.readParcelable(PayloadModel::class.java.classLoader),
        parcel.readBooleanCompat(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeEnum(type)
        parcel.writeAtCompat(createdAt)
        parcel.writeParcelable(actor, flags)
        parcel.writeParcelable(repo, flags)
        parcel.writeParcelable(payload, flags)
        parcel.writeBooleanCompat(publicEvent)
        parcel.writeString(login)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}
