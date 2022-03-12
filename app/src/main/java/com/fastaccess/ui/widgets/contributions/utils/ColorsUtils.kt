package com.fastaccess.ui.widgets.contributions.utils

import android.graphics.Color

/**
 * Copyright 2016 Javier González
 * All right reserved.
 */
object ColorsUtils {
    /**
     * Calculate the value for different color.
     *
     * @param baseColor
     * Value of base color.
     * @param emptyColor
     * Value of empty color
     * @param level
     * Level.
     * @return The value for the level of the base color.
     */
    @JvmStatic
    fun calculateLevelColor(baseColor: Int, emptyColor: Int, level: Int): Int {
        return if (level == 0) {
            emptyColor
        } else Color.rgb(
            calculateR(Color.red(baseColor), level),
            calculateG(Color.green(baseColor), level),
            calculateB(Color.blue(baseColor), level)
        )
    }

    /**
     * Calculate the red value for different level.
     *
     * @param baseR
     * Red value of base color.
     * @param level
     * Level.
     * @return The red value for the level of the base color.
     */
    private fun calculateR(baseR: Int, level: Int): Int {
        return when (level) {
            0 -> 238
            1 -> baseR
            2 -> (baseR * (9 + 46 + 15) / (37f + 9 + 46 + 15)).toInt()
            3 -> (baseR * (46 + 15) / (37f + 9 + 46 + 15)).toInt()
            4 -> (baseR * 15 / (37f + 9 + 46 + 15)).toInt()
            else -> 238
        }
    }

    /**
     * Calculate the green value for different level.
     *
     * @param baseG
     * Green value of base color.
     * @param level
     * Level.
     * @return The green value for the level of the base color.
     */
    private fun calculateG(baseG: Int, level: Int): Int {
        return when (level) {
            0 -> 238
            1 -> baseG
            2 -> (baseG * (35 + 59 + 104) / (32f + 35 + 59 + 104)).toInt()
            3 -> (baseG * (59 + 104) / (32f + 35 + 59 + 104)).toInt()
            4 -> (baseG * 104 / (32f + 35 + 59 + 104)).toInt()
            else -> 238
        }
    }

    /**
     * Calculate the blue value for different level.
     *
     * @param baseB
     * Blue value of base color.
     * @param level
     * Level.
     * @return The blue value for the level of the base color.
     */
    private fun calculateB(baseB: Int, level: Int): Int {
        return when (level) {
            0 -> 238
            1 -> baseB
            2 -> (baseB * (37 + 29 + 35) / (32f + 37 + 29 + 35)).toInt()
            3 -> (baseB * (29 + 35) / (32f + 37 + 29 + 35)).toInt()
            4 -> (baseB * 35 / (32f + 37 + 29 + 35)).toInt()
            else -> 238
        }
    }
}