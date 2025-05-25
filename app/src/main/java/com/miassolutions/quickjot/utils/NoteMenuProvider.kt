package com.miassolutions.quickjot.utils

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import com.miassolutions.quickjot.R

class NoteMenuProvider(
    private val isInSelectionMode: () -> Boolean,
    private val onSearchQueryChanged: (String) -> Unit,
    private val onSortedOrderSelected: (SortOrder) -> Unit,
    private val onSelectAll: () -> Unit,
    private val onDeselectAll: () -> Unit,
    private val onDeleteSelected: () -> Unit,
) : MenuProvider {

    private var searchView: SearchView? = null
    private var searchQueryListener: SearchView.OnQueryTextListener? = null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        clearSearchResources()

        if (isInSelectionMode()) {
            menuInflater.inflate(R.menu.multi_select_menu, menu)
        } else {
            menuInflater.inflate(R.menu.search_menu, menu)
            setupSearchView(menu)
        }
    }

    private fun setupSearchView(menu: Menu) {

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchQueryListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    onSearchQueryChanged(it)
                }
                return true
            }
        }

        searchView.setOnQueryTextListener(searchQueryListener)

    }

    private fun clearSearchResources() {
        searchView?.setOnQueryTextListener(null)
        searchView = null
        searchQueryListener = null
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return if (isInSelectionMode()) {
            handleSelectionModeActions(menuItem)
        } else {
            handleNormalModeActions(menuItem)
        }
    }

    private fun handleSelectionModeActions(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_select_all -> {
                onSelectAll()
                true
            }

            R.id.action_deselec_all -> {
                onDeselectAll()
                true
            }

            R.id.action_delete -> {
                onDeleteSelected()
                true
            }

            else -> false
        }
    }

    private fun handleNormalModeActions(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.sort_title_asc -> {
                onSortedOrderSelected(SortOrder.TITLE_ASC)
                true
            }

            R.id.sort_title_desc -> {
                onSortedOrderSelected(SortOrder.TITLE_DESC)
                true
            }

            R.id.sort_date_asc -> {
                onSortedOrderSelected(SortOrder.TIME_ASC)
                true
            }

            R.id.sort_date_desc -> {
                onSortedOrderSelected(SortOrder.TIME_DESC)
                true
            }

            else -> false
        }
    }


}