package com.miassolutions.quickjot.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.databinding.FragmentNoteListBinding
import com.miassolutions.quickjot.ui.activities.MainActivity
import com.miassolutions.quickjot.ui.adapters.NoteListAdapter
import com.miassolutions.quickjot.ui.viewmodels.NoteViewModel
import com.miassolutions.quickjot.utils.NoteListFragmentMenuProvider
import com.miassolutions.quickjot.utils.NoteListMenuActions
import com.miassolutions.quickjot.utils.SortOrder
import com.miassolutions.quickjot.utils.collectLatestLifecycleFlow
import com.miassolutions.quickjot.utils.showSnackbarMsg
import com.miassolutions.quickjot.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



@AndroidEntryPoint
class NoteListFragment : Fragment(R.layout.fragment_note_list), NoteListMenuActions {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteListAdapter
    private val noteViewModel by viewModels<NoteViewModel>()

    // Hold the state of whether we are in selection mode
    private var isInSelectionMode: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteListBinding.bind(view)

        // Setup back press handling
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isInSelectionMode) {
                        noteViewModel.clearSelection() // Exit selection mode
                    } else {
                        requireActivity().moveTaskToBack(true) // Minimize app
                    }
                }
            }
        )

        setupRecyclerView()
        fabClickListener()
        observeViewModel()
        setupMenuProvider()
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteListAdapter(
            onItemClick = { note ->
                if (!isInSelectionMode) {
                    val action = NoteListFragmentDirections.toAddEditNoteFragment(note)
                    findNavController().navigate(action)
                } else {
                    noteViewModel.toggleSelection(note.noteId)
                }
            },
            onItemLongClick = { note ->
                noteViewModel.toggleSelection(note.noteId)
                true // Consume the long click event
            },
            isSelected = { note ->
                noteViewModel.selectedNoteIds.value.contains(note.noteId)
            }
        )
        binding.rvNotes.adapter = noteAdapter
    }

    private fun fabClickListener() {
        binding.floatingActionButton.setOnClickListener {
            val action = NoteListFragmentDirections.toAddEditNoteFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        collectLatestLifecycleFlow {
            launch {
                noteViewModel.displayedNotes.collectLatest { notes ->
                    noteAdapter.submitList(notes)
                }
            }

            launch {
                noteViewModel.selectedNoteIds.collectLatest { selected ->
                    val newIsInSelectionMode = selected.isNotEmpty()
                    updateToolbar(newIsInSelectionMode, selected.size)
                    noteAdapter.setSelectedItems(selected)

                    // Invalidate menu if selection mode changes
                    if (newIsInSelectionMode != isInSelectionMode) {
                        isInSelectionMode = newIsInSelectionMode
                        requireActivity().invalidateMenu() // Request menu to be re-drawn
                    }

                    // Hide/Show FAB based on selection mode
                    if (isInSelectionMode) {
                        binding.floatingActionButton.hide()
                    } else {
                        binding.floatingActionButton.show()
                    }
                }
            }
        }
    }

    private fun updateToolbar(isSelecting: Boolean, selectedCount: Int) {
        val toolbar =
            (activity as? MainActivity)?.findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar?.let {
            it.title = if (isSelecting) "$selectedCount" else getString(R.string.app_name)

            if (isSelecting) {
                it.setNavigationIcon(R.drawable.ic_close)
                it.setNavigationOnClickListener {
                    noteViewModel.clearSelection()
                }
            } else {
                it.navigationIcon = null // Remove navigation icon
                it.setNavigationOnClickListener(null) // Clear listener
            }
        }
    }

    private fun setupMenuProvider() {
        val menuProvider = NoteListFragmentMenuProvider(
            viewLifecycleOwner,
            menuActions = this,
            getCurrentSelectionMode = { isInSelectionMode }
        )

        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onSortOrderSelected(sortOrder: SortOrder) {
        noteViewModel.sortOrder(sortOrder)
    }

    override fun onQueryTextChange(query: String) {
        noteViewModel.updateQuery(query)
    }

    override fun onDeleteSelectedNotes() {
        noteViewModel.deleteSelectedItems()
        val notesSize =noteViewModel.selectedNoteIds.value.size
        val msg = if (notesSize == 1) "1 note" else "$notesSize notes deleted"
        showSnackbarMsg(msg)
    }

    override fun onSelectAllNotes() {
        noteViewModel.selectAll()
    }

    override fun onDeselectAllNotes() {
        noteViewModel.clearSelection()
    }
}