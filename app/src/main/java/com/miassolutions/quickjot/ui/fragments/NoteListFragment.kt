package com.miassolutions.quickjot.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.data.local.NoteEntity
import com.miassolutions.quickjot.databinding.FragmentNoteListBinding
import com.miassolutions.quickjot.ui.activities.MainActivity
import com.miassolutions.quickjot.ui.adapters.NoteListAdapter
import com.miassolutions.quickjot.ui.viewmodels.NoteViewModel
import com.miassolutions.quickjot.utils.NoteMenuProvider
import com.miassolutions.quickjot.utils.SelectionStateManager
import com.miassolutions.quickjot.utils.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class NoteListFragment : Fragment(R.layout.fragment_note_list) {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteListAdapter
    private val noteViewModel by viewModels<NoteViewModel>()

    private lateinit var selectionStateManager: SelectionStateManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteListBinding.bind(view)

        setupMenuProvider()
        setupToolbar()
        setupRecyclerView()
        fabClickListener()
        observeViewModel()
        setupBackPressedHandler()

    }

    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (noteViewModel.selectedNoteIds.value.isNotEmpty()) {
                        noteViewModel.clearSelection()
                    } else {
                        requireActivity().moveTaskToBack(true)
                    }
                }
            })
    }

    private fun setupToolbar() {
        val toolbar = (activity as MainActivity).findViewById<MaterialToolbar>(R.id.materialToolbar)
        selectionStateManager = SelectionStateManager(toolbar) {
            noteViewModel.clearSelection()
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteListAdapter(
            onItemClick = { note ->
                if (noteViewModel.selectedNoteIds.value.isEmpty()) {
                    navigateToEditNote(note)
                } else {
                    noteViewModel.toggleSelection(note.noteId)
                }
            },
            onItemLongClick = { note ->
                noteViewModel.toggleSelection(note.noteId)
                true
            },
            isSelected = { note ->
                noteViewModel.selectedNoteIds.value.contains(note.noteId)
            }
        )
        binding.rvNotes.adapter = noteAdapter
    }

    private fun navigateToEditNote(note: NoteEntity) {
        val action = NoteListFragmentDirections.toAddEditNoteFragment(note)
        findNavController().navigate(action)
    }

    private fun fabClickListener() {
        binding.floatingActionButton.setOnClickListener {
            val action = NoteListFragmentDirections.toAddEditNoteFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(
            NoteMenuProvider(
                isInSelectionMode = { noteViewModel.selectedNoteIds.value.isNotEmpty() },
                onSearchQueryChanged = { noteViewModel.updateQuery(it) },
                onSortedOrderSelected = { noteViewModel.sortOrder(it) },
                onSelectAll = { noteViewModel.selectAll() },
                onDeselectAll = { noteViewModel.clearSelection() },
                onDeleteSelected = { noteViewModel.deleteSelectedItems() }
            ),
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun observeViewModel() {
        collectLatestLifecycleFlow {
            //for ui collectLatest
            noteViewModel.displayedNotes.collectLatest {
                noteAdapter.submitList(it)
            }


            // collect: Use collect when every single emission matters and needs to be processed sequentially, even if it takes time.

            noteViewModel.selectedNoteIds.collect { selectedIds ->
                val hasSelection = selectedIds.isNotEmpty()
                selectionStateManager.updateSelectionState(selectedIds.size)
                noteAdapter.setSelectedItems(selectedIds)

                if (hasSelection) {
                    binding.floatingActionButton.hide()
                } else {
                    binding.floatingActionButton.show()
                }
                requireActivity().invalidateMenu()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


}


