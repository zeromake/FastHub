package com.fastaccess.helper

import android.text.format.DateUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ParseDateFormat private constructor() {
    private val lock = Any()
    private val dateFormat: DateFormat
    fun format(date: Date?): String {
        synchronized(lock) {
            return if (date == null) DATE_NA else dateFormat.format(date)
        }
    }

    companion object {
        const val DATE_NA = "N/A"
        private val instance = ParseDateFormat()
        fun getTimeAgo(toParse: String?): CharSequence {
            if (toParse.isNullOrEmpty()) {
                return DATE_NA
            }
            try {
                val parsedDate = instance.dateFormat.parse(toParse) ?: return DATE_NA
                val now = System.currentTimeMillis()
                return DateUtils.getRelativeTimeSpanString(
                    parsedDate.time,
                    now,
                    DateUtils.SECOND_IN_MILLIS
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return DATE_NA
        }

        @JvmStatic
        fun getTimeAgo(parsedDate: Date?): CharSequence {
            if (parsedDate != null) {
                val now = System.currentTimeMillis()
                return DateUtils.getRelativeTimeSpanString(
                    parsedDate.time,
                    now,
                    DateUtils.SECOND_IN_MILLIS
                )
            }
            return "N/A"
        }

        @JvmStatic
        fun toGithubDate(date: Date): String {
            return instance.format(date)
        }

        @JvmStatic
        fun prettifyDate(timestamp: Long): String {
            return SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date(timestamp))
        }

        @JvmStatic
        fun getDateFromString(date: String): Date? {
            try {
                return SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }

        @JvmStatic
        fun getDateByDays(days: Int): String {
            val cal = Calendar.getInstance()
            val s = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            cal.add(Calendar.DAY_OF_YEAR, days)
            return s.format(Date(cal.timeInMillis))
        }

        @JvmStatic
        val lastWeekDate: String
            get() = getDateByDays(-7)
    }

    init {
        dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
    }
}