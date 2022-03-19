package com.fastaccess.helper

import android.content.Context
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.fastaccess.R
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import com.fastaccess.data.dao.NotificationSoundModel
import com.fastaccess.provider.crash.Report
import java.io.File
import java.lang.Exception
import java.util.ArrayList

/**
 * Created by kosh20111 on 10/7/2015. CopyRights @
 */
object FileHelper {
    private var PATH: String? = null
    fun getExternalDataPath(context: Context): String {
        if (PATH == null) {
            PATH = context.getExternalFilesDir(null).toString() + File.separator + "FastHub"
        }
        return PATH!!
    }

    const val ONE_MB = 1048576L
    fun getPath(context: Context, uri: Uri): String? {
        var filePath: String? = null
        try {
            val wholeID = DocumentsContract.getDocumentId(uri)
            val id = wholeID.split(":").toTypedArray()[1]
            val column = arrayOf(MediaStore.Images.Media.DATA)
            val sel = MediaStore.Images.Media._ID + "=?"
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            ).use { cursor ->
                if (cursor != null) {
                    val columnIndex = cursor.getColumnIndex(column[0])
                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(columnIndex)
                    }
                }
            }
        } catch (e: Exception) {
            Report.reportCatchException(e)
        }
        return filePath
    }

    fun getRingtoneName(context: Context, uri: Uri?): String {
        var title = context.getString(R.string.sound_chooser_summary)
        if (uri != null) {
            val ringtone = RingtoneManager.getRingtone(context, uri)
            if (ringtone != null) {
                return ringtone.getTitle(context)
            } else {
                try {
                    context.contentResolver.query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Audio.Media.TITLE),
                        MediaStore.Audio.Media._ID + " =?",
                        arrayOf(uri.lastPathSegment),
                        null
                    ).use { cur ->
                        if (cur != null) {
                            title = cur.getString(1)
                            if (InputHelper.isEmpty(title)) {
                                val index = cur.getColumnIndex(MediaStore.Audio.Media.TITLE)
                                title = cur.getString(index)
                            }
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
        }
        return title
    }

    fun getNotificationSounds(
        context: Context?,
        defaultValue: String?
    ): ArrayList<NotificationSoundModel> {
        val notificationSounds = ArrayList<NotificationSoundModel>()
        val ringtoneManager = RingtoneManager(context)
        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION)
        ringtoneManager.cursor.use { ringsCursor ->
            while (ringsCursor.moveToNext()) {
                val title = ringsCursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                val uri = Uri.parse(
                    ringsCursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/"
                            + ringsCursor.getString(RingtoneManager.ID_COLUMN_INDEX)
                )
                val selected = defaultValue != null && (uri.toString().contains(defaultValue) ||
                        title.equals(
                            defaultValue,
                            ignoreCase = true
                        ) || defaultValue.contains(title))
                Logger.e(defaultValue, title, uri, selected)
                notificationSounds.add(NotificationSoundModel(title, uri, selected))
            }
        }
        return notificationSounds
    }

}