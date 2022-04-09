package com.fastaccess.data.dao

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import android.os.Parcel
import com.fastaccess.data.entity.User
import com.fastaccess.github.PullRequestTimelineQuery
import java.util.ArrayList

/**
 * Created by Kosh on 28 Mar 2017, 9:15 PM
 */
open class ReactionsModel : Parcelable {
    var id: Long = 0
    var url: String? = null
    var totalCount = 0

    @SerializedName("+1", alternate = ["thumbs_up"])
    var plusOne = 0

    @SerializedName("-1", alternate = ["thumbs_down"])
    var minusOne = 0
    var laugh = 0
    var hooray = 0
    var confused = 0
    var heart = 0
    var rocket = 0
    var eyes = 0
    var content: String? = null
    var user: User? = null
    var viewerHasReacted = false
    var isCallingApi = false

    constructor()

    override fun toString(): String {
        return "ReactionsModel{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", total_count=" + totalCount +
                ", plusOne=" + plusOne +
                ", minusOne=" + minusOne +
                ", laugh=" + laugh +
                ", hooray=" + hooray +
                ", confused=" + confused +
                ", heart=" + heart +
                ", rocket=" + rocket +
                ", eyes=" + eyes +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(url)
        dest.writeInt(totalCount)
        dest.writeInt(plusOne)
        dest.writeInt(minusOne)
        dest.writeInt(laugh)
        dest.writeInt(hooray)
        dest.writeInt(confused)
        dest.writeInt(heart)
        dest.writeInt(rocket)
        dest.writeInt(eyes)
        dest.writeString(content)
        dest.writeParcelable(user, flags)
        dest.writeByte(if (isCallingApi) 1.toByte() else 0.toByte())
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readLong()
        url = `in`.readString()
        totalCount = `in`.readInt()
        plusOne = `in`.readInt()
        minusOne = `in`.readInt()
        laugh = `in`.readInt()
        hooray = `in`.readInt()
        confused = `in`.readInt()
        heart = `in`.readInt()
        rocket = `in`.readInt()
        eyes = `in`.readInt()
        content = `in`.readString()
        user = `in`.readParcelable(User::class.java.classLoader)
        isCallingApi = `in`.readByte().toInt() != 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReactionsModel> =
            object : Parcelable.Creator<ReactionsModel> {
                override fun createFromParcel(source: Parcel): ReactionsModel? {
                    return ReactionsModel(source)
                }

                override fun newArray(size: Int): Array<ReactionsModel?> {
                    return arrayOfNulls(size)
                }
            }

        fun getReactionGroup(reactions: List<PullRequestTimelineQuery.ReactionGroup>?): List<ReactionsModel> {
            val models: MutableList<ReactionsModel> = ArrayList()
            if (reactions != null && reactions.isNotEmpty()) {
                for ((viewerHasReacted, content, reactors) in reactions) {
                    val model = ReactionsModel()
                    model.content = (content.rawValue)
                    model.viewerHasReacted = (viewerHasReacted)
                    model.totalCount = (reactors.totalCount)
                    models.add(model)
                }
            }
            return models
        }

        fun getReaction(reactions: List<PullRequestTimelineQuery.ReactionGroup1>?): List<ReactionsModel> {
            val models: MutableList<ReactionsModel> = ArrayList()
            if (reactions != null && reactions.isNotEmpty()) {
                for ((viewerHasReacted, content, reactors) in reactions) {
                    val model = ReactionsModel()
                    model.content = content.rawValue
                    model.viewerHasReacted = viewerHasReacted
                    model.totalCount = reactors.totalCount
                    models.add(model)
                }
            }
            return models
        }

        fun getReaction2(reactions: List<PullRequestTimelineQuery.ReactionGroup2>?): List<ReactionsModel> {
            val models: MutableList<ReactionsModel> = ArrayList()
            if (reactions != null && reactions.isNotEmpty()) {
                for ((viewerHasReacted, content, reactors) in reactions) {
                    val model = ReactionsModel()
                    model.content = content.rawValue
                    model.viewerHasReacted = viewerHasReacted
                    model.totalCount = reactors.totalCount
                    models.add(model)
                }
            }
            return models
        }
    }
}