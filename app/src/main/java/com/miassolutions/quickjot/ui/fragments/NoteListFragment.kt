package com.miassolutions.quickjot.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.databinding.FragmentNoteListBinding
import com.miassolutions.quickjot.ui.adapters.NoteListAdapter
import com.miassolutions.quickjot.ui.viewmodels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "NoteListFragment"

@AndroidEntryPoint
class NoteListFragment : Fragment(R.layout.fragment_note_list) {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteListAdapter
    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteListBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
        setupUI()

    }

    private fun setupUI() {
        binding.floatingActionButton.setOnClickListener {
            val action = NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteViewModel.notes.collect {
                    Log.d(TAG, "$it")
                    noteAdapter.submitList(it)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteListAdapter(
            onDeleteClick = { note ->
                Snackbar.make(binding.root, "Deleted", Snackbar.LENGTH_LONG).setAction("Undo") {

                    noteViewModel.insertAgain(note)
                }.show()
                noteViewModel.deleteNote(note)
            }, onItemClick = { note ->
                val action =
                    NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(note)
                findNavController().navigate(action)
            }
        )


        binding.rvNotes.adapter = noteAdapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}