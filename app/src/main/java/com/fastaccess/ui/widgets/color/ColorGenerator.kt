package com.fastaccess.ui.widgets.color

import android.content.Context
import androidx.annotation.ColorInt
import com.fastaccess.helper.AppHelper
import kotlin.math.abs

class ColorGenerator private constructor(private val colors: List<Int>) {
    companion object {
        private var MATERIAL: ColorGenerator? = null
        private var MATERIAL_DARK: ColorGenerator? = null
        private fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }

        @ColorInt
        fun getColor(context: Context, ss: String?): Int {
            return if (AppHelper.isNightMode(context.resources)) {
                MATERIAL_DARK!!.getColor(ss)
            } else {
                MATERIAL!!.getColor(ss)
            }
        }

        init {
            MATERIAL = create(
                listOf(
                    -0xe6892e,
                    -0xff7c71,
                    -0xaed258,
                    -0xd182ce,
                    -0xd7ca6d,
                    -0xfea865,
                    -0x3aee9e,
                    -0x95e466,
                    -0x2b0000,
                    -0xff96a4
                )
            )
            MATERIAL_DARK = create(
                listOf(
                    -0x3ef9,
                    -0x3c00,
                    -0xde690d,
                    -0xd68601,
                    -0x5e7781,
                    -0xb22f1f,
                    -0xff533f,
                    -0x19b5e7,
                    -0x6a8a33,
                    -0x994496
                )
            )
        }
    }

    private fun getColor(key: String?): Int {
        return colors[abs((key ?: "default").hashCode()) % colors.size]
    }
}