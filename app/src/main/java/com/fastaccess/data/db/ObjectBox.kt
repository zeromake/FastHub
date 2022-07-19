package com.fastaccess.data.db

import android.content.Context
import com.fastaccess.data.entity.MyObjectBox
import io.objectbox.BoxStore


class ObjectBox {
    companion object {
        lateinit var boxStore: BoxStore
        fun init(context: Context) {
            boxStore = MyObjectBox.builder()
                .androidContext(context.applicationContext)
                .build()
//            if (BuildConfig.DEBUG) {
//                val started = AndroidObjectBrowser(boxStore).start(context)
//                Log.i("ObjectBrowser", "Started: $started")
//            }
        }
    }
}
