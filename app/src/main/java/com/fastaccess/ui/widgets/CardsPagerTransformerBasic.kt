package com.fastaccess.ui.widgets

import android.view.View
import androidx.viewpager.widget.ViewPager

class CardsPagerTransformerBasic(private val baseElevation: Int, private val raisingElevation: Int) : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val absPosition = Math.abs(position)
        if (absPosition >= 1) {
            page.elevation = baseElevation.toFloat()
        } else {
            page.elevation = (1 - absPosition) * raisingElevation + baseElevation
        }
    }


}