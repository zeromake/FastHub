package com.fastaccess.ui.widgets

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.ViewHelper
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener


/**
 * Created by Hamad on 6/11/17.
 */
class ColorPickerPreference : Preference, OnChooseColorListener {
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        widgetLayoutResource = R.layout.preference_widget_color
    }

    override fun onClick() {
        super.onClick()
        val selectedColor1 = selectedColor
        val title = String.format("Accent Color: (Currently: %s)", selectedColorName)
        val colorPicker = ColorPicker(findActivity(context))
        colorPicker.setRoundColorButton(true)
        colorPicker.setColors(R.array.theme_colors_hex)
        colorPicker.setDefaultColorButton(selectedColor1)
        colorPicker.setTitle(title)
        val titleTv = colorPicker.dialogViewLayout.findViewById<TextView>(R.id.title)
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        colorPicker.positiveButton.setTextColor(ViewHelper.getPrimaryTextColor(context))
        colorPicker.negativeButton.setTextColor(ViewHelper.getPrimaryTextColor(context))
        colorPicker.setOnChooseColorListener(this)
        colorPicker.show()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val colorButton = holder.findViewById(R.id.color) as Button
        colorButton.setBackgroundResource(R.drawable.circle_shape)
        val filter = PorterDuffColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
        colorButton.background.colorFilter = filter
    }

    private val selectedColor: Int
        get() {
            val colorTypedArray = context.resources.obtainTypedArray(R.array.theme_colors_hex)
            val colorNames = context.resources.getStringArray(R.array.theme_colors)
            val colors = ArrayList<Int>()
            for (i in 0 until colorTypedArray.length()) {
                colors.add(colorTypedArray.getColor(i, 0))
            }
            colorTypedArray.recycle()
            val preferenceValueToColor = HashMap<Int, Int>()
            for (i in colorNames.indices) {
                preferenceValueToColor[PrefGetter.getThemeColor(context.resources, colorNames[i])] =
                    colors[i]
            }
            return preferenceValueToColor[PrefGetter.getThemeColor(context)]!!
        }

    override fun onChooseColor(position: Int, color: Int) {
        // put code
        //getOnPreferenceChangeListener().onPreferenceChange(ColorPickerPreference.this, color);
        persistString(context.resources.getStringArray(R.array.theme_colors)[position])
        onPreferenceChangeListener.onPreferenceChange(
            this,
            context.resources.getStringArray(R.array.theme_colors)[position]
        )
    }

    override fun onCancel() {
        // do nothing when the dialog is canceled
    }

    private val selectedColorName: String
        get() {
            val colorNames = context.resources.getStringArray(R.array.theme_colors)
            return colorNames[PrefGetter.getThemeColor(context) - 1]
        }

    private fun findActivity(pContext: Context): Activity? {
        var context = pContext
        while (true) {
            when (context) {
                is Activity -> {
                    return context
                }
                is ContextWrapper -> {
                    context = context.baseContext
                }
                else -> {
                    return null
                }
            }
        }

    }
}