package com.etienne.glycemialog.utils

import android.content.Context
import android.util.TypedValue

fun getDPValue(context: Context, value: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics).toInt()
}