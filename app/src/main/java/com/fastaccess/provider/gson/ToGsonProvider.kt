package com.fastaccess.provider.gson

import android.content.Context
import com.fastaccess.R
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Created by Kosh on 26 Mar 2017, 10:07 PM
 */
object ToGsonProvider {
    @JvmStatic
    fun getChangelog(context: Context): Observable<String?> {
        return Observable.fromCallable {
            context.resources.openRawResource(R.raw.changelog).use { `is` ->
                try {
                    ByteArrayOutputStream().use { byteStream ->
                        val buffer = ByteArray(`is`.available())
                        `is`.read(buffer) //ignore lint
                        byteStream.write(buffer)
                        return@fromCallable byteStream.toString()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            null
        }
    }
}