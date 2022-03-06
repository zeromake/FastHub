package com.fastaccess.data.dao

import android.net.Uri
import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.PullsIssuesParser
import com.fastaccess.helper.InputHelper
import java.lang.NumberFormatException

/**
 * Created by Kosh on 17 Dec 2016, 12:17 AM
 */
open class PullsIssuesParser : Parcelable {
    var login: String? = null
    var repoId: String? = null
    var number = 0
    override fun toString(): String {
        return "PullsIssuesParser{" +
                "login='" + login + '\'' +
                ", repoId='" + repoId + '\'' +
                ", number=" + number +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(login)
        dest.writeString(repoId)
        dest.writeInt(number)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        login = `in`.readString()
        repoId = `in`.readString()
        number = `in`.readInt()
    }

    companion object {
        @JvmStatic
        fun getForPullRequest(url: String): PullsIssuesParser? {
            val uri = Uri.parse(url)
            val segments = uri.pathSegments
            if (segments == null || segments.size < 3) return null
            var owner: String? = null
            var repo: String? = null
            var number: String? = null
            if (segments.size > 3) {
                if ("pull" == segments[2] || "pulls" == segments[2]) {
                    owner = segments[0]
                    repo = segments[1]
                    number = segments[3]
                } else if (("pull" == segments[3] || "pulls" == segments[3]) && segments.size > 4) {
                    owner = segments[1]
                    repo = segments[2]
                    number = segments[4]
                } else {
                    return null
                }
            }
            if (InputHelper.isEmpty(number)) return null
            val issueNumber: Int
            issueNumber = try {
                number!!.toInt()
            } catch (nfe: NumberFormatException) {
                return null
            }
            if (issueNumber < 1) return null
            val model = PullsIssuesParser()
            model.login = owner
            model.repoId = repo
            model.number = issueNumber
            return model
        }

        @JvmStatic
        fun getForIssue(url: String): PullsIssuesParser? {
            val uri = Uri.parse(url)
            val segments = uri.pathSegments
            if (segments == null || segments.size < 3) return null
            var owner: String? = null
            var repo: String? = null
            var number: String? = null
            if (segments.size > 3) {
                if (segments[2].equals("issues", ignoreCase = true)) {
                    owner = segments[0]
                    repo = segments[1]
                    number = segments[3]
                } else if (segments[3].equals("issues", ignoreCase = true) && segments.size > 4) {
                    owner = segments[1]
                    repo = segments[2]
                    number = segments[4]
                } else {
                    return null
                }
            }
            if (InputHelper.isEmpty(number)) return null
            val issueNumber: Int = try {
                number!!.toInt()
            } catch (nfe: NumberFormatException) {
                return null
            }
            if (issueNumber < 1) return null
            val model = PullsIssuesParser()
            model.login = owner
            model.repoId = repo
            model.number = issueNumber
            return model
        }

        @JvmField
        val CREATOR: Parcelable.Creator<PullsIssuesParser> =
            object : Parcelable.Creator<PullsIssuesParser> {
                override fun createFromParcel(source: Parcel): PullsIssuesParser? {
                    return PullsIssuesParser(source)
                }

                override fun newArray(size: Int): Array<PullsIssuesParser?> {
                    return arrayOfNulls(size)
                }
            }
    }
}