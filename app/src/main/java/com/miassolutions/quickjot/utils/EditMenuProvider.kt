package com.miassolutions.quickjot.utils

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.data.local.NoteEntity

class EditMenuProvider(
    private val onShareAction: () -> Unit,
    private val onDeleteAction: () -> Unit,
) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.share_action -> {
                onShareAction()
                true
            }

            R.id.edit_delete_action -> {
                onDeleteAction()
                true
            }

            else -> false
        }
    }
}