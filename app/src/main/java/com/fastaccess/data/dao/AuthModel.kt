package com.fastaccess.data.dao

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import android.os.Parcel
import com.fastaccess.data.dao.AuthModel

/**
 * Created by Kosh on 12 Mar 2017, 3:16 AM
 */
class AuthModel : Parcelable {
    var clientId: String? = null
    var clientSecret: String? = null
    var redirectUri: String? = null
    var scopes: List<String>? = null
    var state: String? = null
    var note: String? = null
    var noteUrl: String? = null

    @SerializedName("X-GitHub-OTP")
    var otpCode: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(clientId)
        dest.writeString(clientSecret)
        dest.writeString(redirectUri)
        dest.writeStringList(scopes)
        dest.writeString(state)
        dest.writeString(note)
        dest.writeString(noteUrl)
        dest.writeString(otpCode)
    }

    private constructor(`in`: Parcel) {
        clientId = `in`.readString()
        clientSecret = `in`.readString()
        redirectUri = `in`.readString()
        scopes = `in`.createStringArrayList()
        state = `in`.readString()
        note = `in`.readString()
        noteUrl = `in`.readString()
        otpCode = `in`.readString()
    }

    companion object CREATOR : Parcelable.Creator<AuthModel> {
        override fun createFromParcel(parcel: Parcel): AuthModel {
            return AuthModel(parcel)
        }

        override fun newArray(size: Int): Array<AuthModel?> {
            return arrayOfNulls(size)
        }
    }
}