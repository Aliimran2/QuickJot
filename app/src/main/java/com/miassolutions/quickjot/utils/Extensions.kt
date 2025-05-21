package com.miassolutions.quickjot.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toFormattedDate(): String {
    return SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(this)
}