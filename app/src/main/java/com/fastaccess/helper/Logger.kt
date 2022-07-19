package com.fastaccess.helper

import android.util.Log
import com.fastaccess.BuildConfig

/**
 * Created by Kosh on 04/12/15 11:52 PM. copyrights @
 */
object Logger {
    private val TAG = Logger::class.java.simpleName
    private val DEBUG = BuildConfig.DEBUG
    private fun e(tag: String, text: Any?) {
        if (!DEBUG) return
        Log.e(tag, text?.toString() ?: "LOGGER IS NULL") //avoid null
    }

    private fun d(tag: String, text: Any?) {
        if (!DEBUG) return
        Log.d(tag, text?.toString() ?: "LOGGER IS NULL") //avoid null
    }

    private fun i(tag: String, text: Any?) {
        if (!DEBUG) return
        Log.i(tag, text?.toString() ?: "LOGGER IS NULL") //avoid null
    }

    @JvmStatic
    fun d(text: Any?) {
        d("$currentClassName || $currentMethodName", text) //avoid null
    }

    @JvmStatic
    fun i(text: Any?) {
        i("$currentClassName || $currentMethodName", text) //avoid null
    }

    @JvmStatic
    fun e(vararg objects: Any?) {
        if (objects.isNotEmpty()) {
            e("$currentClassName || $currentMethodName", objects.contentToString())
        } else {
            e("$currentClassName || $currentMethodName", currentMethodName)
        }
    }

    @JvmStatic
    fun e(objects: List<Any?>?) {
        if (objects != null) {
            e(
                "$currentClassName || $currentMethodName",
                objects.toTypedArray().contentToString()
            )
        } else {
            e(TAG, null)
        }
    }

    private val currentMethodName: String
        get() {
            try {
                return Thread.currentThread().stackTrace[4].methodName + "()"
            } catch (ignored: Exception) {
            }
            return TAG
        }
    private val currentClassName: String
        get() {
            try {
                var className = Thread.currentThread().stackTrace[4].className
                val temp = className.split("[.]").toTypedArray()
                className = temp[temp.size - 1]
                return className
            } catch (ignored: Exception) {
            }
            return TAG
        }
}