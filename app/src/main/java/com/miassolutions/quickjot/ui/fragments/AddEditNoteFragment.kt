package com.miassolutions.quickjot.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.data.local.NoteEntity
import com.miassolutions.quickjot.databinding.FragmentAddEditNoteBinding
import com.miassolutions.quickjot.ui.activities.MainActivity
import com.miassolutions.quickjot.ui.viewmodels.NoteViewModel
import com.miassolutions.quickjot.utils.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditNoteFragment : Fragment(R.layout.fragment_add_edit_note) {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private var currentNote: NoteEntity? = null
    private val args by navArgs<AddEditNoteFragmentArgs>()

    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditNoteBinding.bind(view)

        updateNoteIfNotNull()
        backPressHandler()

    }

    private fun updateNoteIfNotNull() {
        currentNote = args.noteEntity
        currentNote?.let {
            binding.apply {
                etTitle.setText(it.title)
                etContent.setText(it.content)
                tvCreatedEditedAt.text = "Edited: ${it.updatedAt.toFormattedDate()}"
            }
        }
    }


    private fun saveNoteOnExit() {
        binding.apply {


            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (currentNote != null) {
                val hasChanged =
                    currentNote?.let { it.title != title || it.content != content }!!

                if (title.isBlank() && content.isBlank()) return@apply

                if (hasChanged) {
                    val updatedNote = currentNote!!.copy(title = title, content = content)
                    noteViewModel.updateNote(updatedNote)
                    tvCreatedEditedAt.text = updatedNote.updatedAt.toFormattedDate()
                    Toast.makeText(requireContext(), "Note edited", Toast.LENGTH_SHORT).show()

                }


            } else {

                if (title.isBlank() && content.isBlank()) return@apply

                noteViewModel.insertNote(title = title, content = content)
                Toast.makeText(requireContext(), "Note saved", Toast.LENGTH_SHORT).show()
            }
        }

    }


    fun backPressHandler() {

        val activity = (activity as MainActivity)

        val toolbar = activity.findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar.setNavigationOnClickListener {
            saveNoteOnExit()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveNoteOnExit()
                    findNavController().navigateUp()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}