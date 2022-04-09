package com.fastaccess.data.db

import android.content.Context
import android.util.Log
import com.fastaccess.BuildConfig
import com.fastaccess.data.entity.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser


class ObjectBox {
    companion object {
        lateinit var boxStore: BoxStore
        fun init(context: Context) {
            boxStore = MyObjectBox.builder()
                .androidContext(context.applicationContext)
                .build()
            if (BuildConfig.DEBUG) {
                val started = AndroidObjectBrowser(boxStore).start(context)
                Log.i("ObjectBrowser", "Started: $started")
            }
        }
    }
}
