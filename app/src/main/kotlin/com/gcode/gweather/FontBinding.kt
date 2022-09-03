package com.gcode.gweather

import android.graphics.Typeface
import androidx.databinding.BindingConversion
import com.gcode.vasttools.helper.ContextHelper

object FontBinding {
    @BindingConversion
    @JvmStatic
    fun convertStringToFace(s: String?): Typeface {
        return try {
            Typeface.createFromAsset(ContextHelper.getAppContext().assets, s)
        } catch (e: Exception) {
            throw e
        }
    }
}