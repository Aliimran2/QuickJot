package com.miassolutions.quickjot.utils

import android.graphics.Color
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.quickjot.R

class SelectionStateManager(
    private val toolbar: MaterialToolbar,
    private val onSelectionCleared: () -> Unit,
) {
    private var selectedCount = 0
    private val originalTitle = toolbar.title
    private val originalBackground = toolbar.background
    private val originalNavIcon = toolbar.navigationIcon

    fun updateSelectionState(count: Int) {
        selectedCount = count
        val hasSelection = count > 0

        toolbar.title = if (hasSelection) count.toString() else originalTitle
        toolbar.setBackgroundColor(
            if (hasSelection) Color.LTGRAY else Color.TRANSPARENT
        )

        if (hasSelection) {
            toolbar.setNavigationIcon(R.drawable.ic_close)
            toolbar.setNavigationOnClickListener {
                onSelectionCleared()
            }
        } else {
            toolbar.navigationIcon = originalNavIcon
            toolbar.setNavigationOnClickListener(null)
        }
    }
}