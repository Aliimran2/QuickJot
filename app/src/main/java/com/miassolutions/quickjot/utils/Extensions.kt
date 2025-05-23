package com.miassolutions.quickjot.utils

import android.app.Activity
import android.view.View

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toFormattedDate(): String {
    return SimpleDateFormat("dd-MM-yyy hh:mm a", Locale.getDefault()).format(this)
}

fun Activity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showSnackbarMsg(msg: String) {
    view?.let { fragmentView ->
        Snackbar.make(fragmentView, msg, Snackbar.LENGTH_SHORT).show()

    }
}

fun LifecycleOwner.collectLatestLifecycleFlow(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit,
) {
    lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}