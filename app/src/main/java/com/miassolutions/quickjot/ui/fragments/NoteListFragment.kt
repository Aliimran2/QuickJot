package com.miassolutions.quickjot.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.databinding.FragmentNoteListBinding
import com.miassolutions.quickjot.ui.activities.MainActivity
import com.miassolutions.quickjot.ui.adapters.NoteListAdapter
import com.miassolutions.quickjot.ui.viewmodels.NoteViewModel
import com.miassolutions.quickjot.utils.SortOrder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "NoteListFragment"

@AndroidEntryPoint
class NoteListFragment : Fragment(R.layout.fragment_note_list) {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteListAdapter
    private val noteViewModel by viewModels<NoteViewModel>()

    private var defaultMenuProvider: MenuProvider? = null
    private var selectionMenuProvider: MenuProvider? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteListBinding.bind(view)

        setupRecyclerView()
        setupUI()
        observeViewModel()

        setupDefaultMenuProvider()
        requireActivity().addMenuProvider(defaultMenuProvider!!, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteListAdapter(
            onItemClick = { note ->
                noteViewModel.toggleSelection(note.noteId)
            },
            onItemLongClick = { note ->
                noteViewModel.toggleSelection(note.noteId)
                true
            }
        )

        binding.rvNotes.adapter = noteAdapter
    }

    private fun setupUI() {
        binding.floatingActionButton.setOnClickListener {
            val action = NoteListFragmentDirections.toAddEditNoteFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteViewModel.displayedNotes.collectLatest { notes ->
                    Log.d(TAG, "Notes: $notes")
                    noteAdapter.submitList(notes)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteViewModel.selectedNoteIds.collectLatest { selected ->
                    val isSelecting = selected.isNotEmpty()

                    val toolbar = (activity as MainActivity).findViewById<MaterialToolbar>(R.id.materialToolbar)
                    toolbar.title = if (isSelecting) "${selected.size} selected" else "Notes"
                    toolbar.setNavigationIcon(R.drawable.ic_close)
                    toolbar.setNavigationOnClickListener {
                        noteViewModel.clearSelection()
                    }

                    if (isSelecting) {
                        // Switch to selection mode menu
                        removeMenuProvider(defaultMenuProvider)
                        if (selectionMenuProvider == null) setupSelectionMenuProvider()
                        addMenuProvider(selectionMenuProvider)
                    } else {
                        // Switch to default menu
                        toolbar.navigationIcon = null
                        toolbar.setNavigationOnClickListener(null)
                        removeMenuProvider(selectionMenuProvider)
                        addMenuProvider(defaultMenuProvider)
                    }
                }
            }
        }
    }

    private fun setupDefaultMenuProvider() {
        defaultMenuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false

                    override fun onQueryTextChange(newText: String?): Boolean {
                        noteViewModel.updateQuery(newText.orEmpty())
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.sort_title_asc -> {
                        noteViewModel.sortOrder(SortOrder.TITLE_ASC)
                        true
                    }
                    R.id.sort_title_desc -> {
                        noteViewModel.sortOrder(SortOrder.TITLE_DESC)
                        true
                    }
                    R.id.sort_date_asc -> {
                        noteViewModel.sortOrder(SortOrder.TIME_ASC)
                        true
                    }
                    R.id.sort_date_desc -> {
                        noteViewModel.sortOrder(SortOrder.TIME_DESC)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupSelectionMenuProvider() {
        selectionMenuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.multi_select_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_delete -> {
                        noteViewModel.deleteSelectedItems()
                        true
                    }
                    R.id.action_select_all -> {
                        noteViewModel.selectAll()
                        true
                    }
                    R.id.action_deselec_all -> {
                        noteViewModel.clearSelection()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun addMenuProvider(provider: MenuProvider?) {
        provider?.let {
            requireActivity().addMenuProvider(it, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    private fun removeMenuProvider(provider: MenuProvider?) {
        provider?.let {
            requireActivity().removeMenuProvider(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        removeMenuProvider(defaultMenuProvider)
        removeMenuProvider(selectionMenuProvider)
    }
}
