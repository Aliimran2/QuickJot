package com.miassolutions.quickjot.utils

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.LifecycleOwner
import com.miassolutions.quickjot.R

class NoteListFragmentMenuProvider(
    private val lifecycleOwner: LifecycleOwner,
    private val menuActions: NoteListMenuActions,
    private val getCurrentSelectionMode: () -> Boolean,
) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        val isSelectionMode = getCurrentSelectionMode()
        if (isSelectionMode) {
            menuInflater.inflate(R.menu.multi_select_menu, menu)
        } else {
            menuInflater.inflate(R.menu.search_menu, menu)

            val searchItem = menu.findItem(R.id.action_search)
            val searchView = searchItem.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    menuActions.onQueryTextChange(newText.orEmpty())
                    return true
                }
            })
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val isInSelectionMode = getCurrentSelectionMode()
        return if (isInSelectionMode) {
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    menuActions.onDeleteSelectedNotes()
                    true
                }

                R.id.action_select_all -> {
                    menuActions.onSelectAllNotes()
                    true
                }

                R.id.action_deselec_all -> {
                    menuActions.onDeselectAllNotes()
                    true
                }

                else -> false
            }
        } else {
            when (menuItem.itemId) {
                R.id.sort_title_asc -> {
                    menuActions.onSortOrderSelected(SortOrder.TITLE_ASC)
                    true
                }

                R.id.sort_title_desc -> {
                    menuActions.onSortOrderSelected(SortOrder.TITLE_DESC)
                    true
                }

                R.id.sort_date_asc -> {
                    menuActions.onSortOrderSelected(SortOrder.TIME_ASC)
                    true
                }

                R.id.sort_date_desc -> {
                    menuActions.onSortOrderSelected(SortOrder.TIME_DESC)
                    true
                }

                else -> false
            }
        }
    }
}