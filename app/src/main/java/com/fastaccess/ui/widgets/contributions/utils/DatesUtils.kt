package com.fastaccess.ui.widgets.contributions.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright 2016 Javier González
 * All right reserved.
 */
object DatesUtils {
    /**
     * Get the day of week from a date.
     * 0 for SUN.
     * 1 for MON.
     * .
     * .
     * .
     * 6 for SAT.
     *
     * @param year
     * The year of the date.
     * @param month
     * The month of the date.
     * @param day
     * The day of month of the date.
     * @return Integer to determine the day of week.
     */
    @JvmStatic
    @SuppressLint("WrongConstant")
    fun getWeekDayFromDate(year: Int, month: Int, day: Int): Int {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = day
        calendar.add(Calendar.SECOND, 0)
        calendar.firstDayOfWeek = Calendar.SUNDAY
        return calendar[Calendar.DAY_OF_WEEK] - 1
    }

    /**
     * Get the short month name for a certain date.
     *
     * @param year
     * The year of the date.
     * @param month
     * The month of the date.
     * @param day
     * The day of the date.
     * @return The short name of the month.
     */
    @JvmStatic
    @SuppressLint("WrongConstant")
    fun getShortMonthName(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = day
        calendar.add(Calendar.SECOND, 0)
        val monthDate = SimpleDateFormat("MMM", Locale.US)
        return monthDate.format(calendar.time)
    }

    /**
     * Return if the date given is a first week of mount
     *
     * @param year
     * The year of the date.
     * @param month
     * The month of the date.
     * @param day
     * The day of the date.
     * @return true or false
     */
    @JvmStatic
    @SuppressLint("WrongConstant")
    fun isFirstWeekOfMount(year: Int, month: Int, day: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = day
        calendar.add(Calendar.SECOND, 0)
        calendar.firstDayOfWeek = Calendar.SUNDAY
        return calendar[Calendar.DAY_OF_WEEK_IN_MONTH] == 1
    }

    /**
     * Return if the date given is a first day of week
     *
     * @param year
     * The year of the date.
     * @param month
     * The month of the date.
     * @param day
     * The day of the date.
     * @return true or false
     */
    @JvmStatic
    @SuppressLint("WrongConstant")
    fun isFirstDayOfWeek(year: Int, month: Int, day: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = day
        calendar.add(Calendar.SECOND, 0)
        calendar.firstDayOfWeek = Calendar.SUNDAY
        return calendar[Calendar.DAY_OF_WEEK] == 1
    }
}