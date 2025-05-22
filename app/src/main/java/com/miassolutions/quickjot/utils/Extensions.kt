package com.miassolutions.quickjot.utils

import android.app.Activity

import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toFormattedDate(): String {
    return SimpleDateFormat("dd-MM-yyy hh:mm a", Locale.getDefault()).format(this)
}

fun Activity.showToast(msg : String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(msg: String){
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}