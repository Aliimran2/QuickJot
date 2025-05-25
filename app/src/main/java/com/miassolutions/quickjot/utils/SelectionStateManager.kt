package com.miassolutions.quickjot.utils

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.quickjot.R

class SelectionStateManager(
    private val toolbar: MaterialToolbar,
    private val onSelectionCleared: () -> Unit,
) {
    private val context = toolbar.context
    private var selectedCount = 0


    fun updateSelectionState(count: Int) {
        selectedCount = count
        val hasSelection = count > 0

        toolbar.title = if (hasSelection) count.toString() else context.getString(R.string.app_name)
        toolbar.setBackgroundColor(
            if (hasSelection) context.getColor(R.color.cabColor) else context.getColor(R.color.toolbarColor)
        )

        if (hasSelection) {
            toolbar.setNavigationIcon(R.drawable.ic_close)
            toolbar.setNavigationOnClickListener {
                onSelectionCleared()
            }
        } else {
            toolbar.navigationIcon = null
            toolbar.setNavigationOnClickListener(null)
        }
    }
}