package com.fastaccess.provider.crash

import timber.log.Timber

class Report {
    companion object {
        fun reportCatchException(e: Exception) {
            Timber.e(e, "FastHub-Re Crash Report")
        }
    }
}