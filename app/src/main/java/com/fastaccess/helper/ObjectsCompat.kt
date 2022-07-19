package com.fastaccess.helper

/**
 * Created by Kosh on 18 Apr 2017, 10:57 PM
 */
object ObjectsCompat {
    @JvmStatic
    fun nonNull(obj: Any?): Boolean {
        return obj != null
    }
}