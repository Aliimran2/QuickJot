package com.miassolutions.quickjot.utils

import android.content.Context
import javax.inject.Inject
import androidx.core.content.edit


class PreferenceManager @Inject constructor(context: Context) {

    private val prefs = context.getSharedPreferences(NOTE_PREFS, Context.MODE_PRIVATE)

    companion object {
        private const val NOTE_PREFS = "note_prefs"
        private const val KEY_SORT_ORDER = "sort_order"
    }

    fun saveSortOrder(sortOrder: SortOrder) {
        prefs.edit { putString(KEY_SORT_ORDER, sortOrder.name) }
    }

    fun getSortType(): SortOrder {
        val name = prefs.getString(KEY_SORT_ORDER, SortOrder.TITLE_DESC.name)
        return try {

            SortOrder.valueOf(name!!)
        } catch (e: IllegalArgumentException) {
            SortOrder.TITLE_DESC
        }
    }
}