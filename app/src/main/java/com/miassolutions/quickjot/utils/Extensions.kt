package com.miassolutions.quickjot.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toFormattedDate(): String {
    return SimpleDateFormat("dd-MM-yyy hh:mm a", Locale.getDefault()).format(this)
}