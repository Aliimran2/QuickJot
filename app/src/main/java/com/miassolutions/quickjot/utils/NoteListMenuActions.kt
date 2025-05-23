package com.miassolutions.quickjot.utils

interface NoteListMenuActions {
    fun onSortOrderSelected(sortOrder: SortOrder)
    fun onQueryTextChange(query : String)
    fun onDeleteSelectedNotes()
    fun onSelectAllNotes()
    fun onDeselectAllNotes()
}