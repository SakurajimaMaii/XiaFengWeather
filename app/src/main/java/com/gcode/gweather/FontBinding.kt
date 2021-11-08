package com.gcode.gweather

import android.graphics.Typeface
import androidx.databinding.BindingConversion
import com.gcode.gweather.utils.AppUtils

object FontBinding {
    @BindingConversion
    @JvmStatic
    fun convertStringToFace(s: String?): Typeface {
        return try {
            Typeface.createFromAsset(AppUtils.context.assets, s)
        } catch (e: Exception) {
            throw e
        }
    }
}