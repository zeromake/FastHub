package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

class GithubStatusComponentsModel(): Parcelable {
    var page: GithubStatusPageModel? = null
    var components: List<GithubStatusComponentModel>? = null

    constructor(parcel: Parcel) : this() {
        page = parcel.readParcelable(GithubStatusPageModel::class.java.classLoader)
        components = parcel.createTypedArrayList(GithubStatusComponentModel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(page, flags)
        parcel.writeTypedList(components)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GithubStatusComponentsModel> {
        override fun createFromParcel(parcel: Parcel): GithubStatusComponentsModel {
            return GithubStatusComponentsModel(parcel)
        }

        override fun newArray(size: Int): Array<GithubStatusComponentsModel?> {
            return arrayOfNulls(size)
        }
    }

}