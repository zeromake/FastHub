package com.fastaccess.data.dao

import android.os.Parcelable
import com.fastaccess.data.dao.FilesListModel
import com.google.gson.annotations.SerializedName
import android.os.Parcel
import com.fastaccess.data.dao.CreateGistModel
import java.util.HashMap

/**
 * Created by Kosh on 18 Feb 2017, 11:15 PM
 */
class CreateGistModel : Parcelable {
    var files: HashMap<String, FilesListModel>? = null
    var description: String? = null

    @SerializedName("public")
    var publicGist: Boolean? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(files)
        dest.writeString(description)
        dest.writeValue(publicGist)
    }

    private constructor(`in`: Parcel) {
        files = `in`.readSerializable() as HashMap<String, FilesListModel>
        description = `in`.readString()
        publicGist = `in`.readValue(Boolean::class.java.classLoader) as Boolean?
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CreateGistModel> =
            object : Parcelable.Creator<CreateGistModel> {
                override fun createFromParcel(source: Parcel): CreateGistModel? {
                    return CreateGistModel(source)
                }

                override fun newArray(size: Int): Array<CreateGistModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}