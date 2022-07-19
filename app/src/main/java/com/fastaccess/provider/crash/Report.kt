package com.fastaccess.provider.crash

import android.content.Context
import com.fastaccess.BuildConfig
import com.tencent.bugly.crashreport.CrashReport
import timber.log.Timber

class Report {
    companion object {
        fun init(context: Context) {
            CrashReport.initCrashReport(context, "ae44f59ed7", BuildConfig.DEBUG)
        }

        fun reportCatchException(e: Exception) {
            Timber.e(e, "report catch")
            CrashReport.postCatchedException(e)
        }
    }
}