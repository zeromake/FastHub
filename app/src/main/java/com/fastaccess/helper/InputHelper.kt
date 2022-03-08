package com.fastaccess.helper

import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by kosh20111 on 3/11/2015. CopyRights @
 *
 *
 * Input Helper to validate stuff related to input fields.
 */
object InputHelper {
    const val SPACE = "\u202F\u202F"

    @JvmStatic
    private fun isWhiteSpaces(s: String?): Boolean {
        return s != null && s.matches("\\s+".toRegex())
    }

    @JvmStatic
    fun isEmpty(text: String?): Boolean {
        return text == null || TextUtils.isEmpty(text) || isWhiteSpaces(text) || text.equals(
            "null",
            ignoreCase = true
        )
    }

    @JvmStatic
    fun isEmpty(text: Any?): Boolean {
        return text == null || isEmpty(text.toString())
    }

    @JvmStatic
    fun isEmpty(text: EditText?): Boolean {
        return text == null || isEmpty(text.text.toString())
    }

    @JvmStatic
    fun isEmpty(text: TextView?): Boolean {
        return text == null || isEmpty(text.text.toString())
    }

    @JvmStatic
    fun isEmpty(txt: TextInputLayout?): Boolean {
        return txt == null || isEmpty(txt.editText)
    }

    @JvmStatic
    fun toString(editText: EditText): String {
        return editText.text.toString()
    }

    @JvmStatic
    fun toString(editText: TextView): String {
        return editText.text.toString()
    }

    @JvmStatic
    fun toString(textInputLayout: TextInputLayout): String {
        return if (textInputLayout.editText != null) toString(textInputLayout.editText) else ""
    }

    @JvmStatic
    fun toNA(value: String?): String {
        return if (isEmpty(value)) "N/A" else value!!
    }

    @JvmStatic
    fun toString(`object`: Any?): String {
        return if (!isEmpty(`object`)) `object`.toString() else ""
    }

    @JvmStatic
    fun toLong(textView: TextView): Long {
        return toLong(toString(textView))
    }

    @JvmStatic
    fun toLong(text: String): Long {
        if (!isEmpty(text)) {
            try {
                return java.lang.Long.valueOf(text.replace("[^0-9]".toRegex(), ""))
            } catch (ignored: NumberFormatException) {
            }
        }
        return 0
    }

    @JvmStatic
    fun getSafeIntId(id: Long): Int {
        return if (id > Int.MAX_VALUE) (id - Int.MAX_VALUE).toInt() else id.toInt()
    }

    @JvmStatic
    fun capitalizeFirstLetter(s: String): String {
        if (isEmpty(s)) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
}