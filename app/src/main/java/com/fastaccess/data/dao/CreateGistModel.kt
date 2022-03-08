package com.fastaccess.data.dao

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.HashMap

/**
 * Created by Kosh on 18 Feb 2017, 11:15 PM
 */
@Parcelize
class CreateGistModel(
    var files: HashMap<String, FilesListModel>?,
    var description: String?,
    @SerializedName("public") var publicGist: Boolean
) : Parcelable