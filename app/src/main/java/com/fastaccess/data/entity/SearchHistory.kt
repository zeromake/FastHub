package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class SearchHistory(
    @Id(assignable = true)
    var id: Long  = 0,
    @Unique
    var text: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()
    )

    override fun toString(): String {
        return text?:""
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as SearchHistory
        return if (text != null) text == that.text else that.text == null
    }

    override fun hashCode(): Int {
        return if (text != null) text.hashCode() else 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchHistory> {
        override fun createFromParcel(parcel: Parcel): SearchHistory {
            return SearchHistory(parcel)
        }

        override fun newArray(size: Int): Array<SearchHistory?> {
            return arrayOfNulls(size)
        }
    }
}
