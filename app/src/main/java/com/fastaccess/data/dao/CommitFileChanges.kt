package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.utils.ParcelUtil
import io.reactivex.Observable
import java.util.ArrayList

/**
 * Created by Kosh on 20 Jun 2017, 7:32 PM
 */
class CommitFileChanges : Parcelable {
    var linesModel: List<CommitLinesModel>? = null
    var commitFileModel: CommitFileModel? = null

    private constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(linesModel)
        dest.writeParcelable(commitFileModel, flags)
    }

    private constructor(`in`: Parcel) {
        linesModel = `in`.createTypedArrayList(CommitLinesModel.CREATOR)
        commitFileModel = `in`.readParcelable(CommitFileModel::class.java.classLoader)
    }

    override fun toString(): String {
        return "CommitFileChanges{" +
                "linesModel=" + linesModel +
                ", commitFileModel=" + commitFileModel +
                '}'
    }

    companion object {
        @JvmStatic
        fun constructToObservable(files: ArrayList<CommitFileModel>?): Observable<CommitFileChanges> {
            return if (files == null || files.isEmpty()) Observable.empty() else Observable.fromIterable(
                construct(files)
            )
        }

        @JvmStatic
        fun construct(files: List<CommitFileModel>?): List<CommitFileChanges> {
            files ?: return listOf()

            return if (files.isEmpty()) {
                listOf()
            } else files
                .map { m: CommitFileModel -> getCommitFileChanges(m) }
        }

        @JvmStatic
        private fun getCommitFileChanges(m: CommitFileModel): CommitFileChanges {
            val model = CommitFileChanges()
            model.linesModel = CommitLinesModel.getLines(m.patch)
            if (m.patch != null) {
                m.patch = "fake"
            }
            model.commitFileModel = m
            return model
        }

        @JvmField
        val CREATOR: Parcelable.Creator<CommitFileChanges> =
            ParcelUtil.createParcel { CommitFileChanges(it) }

        @JvmStatic
        fun canAttachToBundle(model: CommitFileChanges): Boolean {
            val parcel = Parcel.obtain()
            model.writeToParcel(parcel, 0)
            val size = parcel.dataSize()
            return size < 600000
        }
    }
}