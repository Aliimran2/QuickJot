package com.miassolutions.quickjot.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ShareCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.data.local.NoteEntity
import com.miassolutions.quickjot.databinding.FragmentAddEditNoteBinding
import com.miassolutions.quickjot.ui.activities.MainActivity
import com.miassolutions.quickjot.ui.viewmodels.NoteViewModel
import com.miassolutions.quickjot.utils.EditMenuProvider
import com.miassolutions.quickjot.utils.showSnackbarMsg
import com.miassolutions.quickjot.utils.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditNoteFragment : Fragment(R.layout.fragment_add_edit_note), MenuProvider {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private var currentNote: NoteEntity? = null
    private val args by navArgs<AddEditNoteFragmentArgs>()

    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditNoteBinding.bind(view)

//        val menuHost : MenuHost = requireActivity()
//        menuHost.addMenuProvider(
//            EditMenuProvider(
//                onShareAction = TODO(),
//                onDeleteAction = TODO()
//            ),
//            viewLifecycleOwner,
//            Lifecycle.State.RESUMED
//        )

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

    @SuppressLint("ServiceCast")
    override fun onResume() {
        super.onResume()
        binding.etContent.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.etContent, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onPause() {
        super.onPause()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.etContent.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {


            R.id.share_action -> {
                val note = args.noteEntity.run {
                    this?.title + "\n" + this?.content
                }

                val sharingIntent = ShareCompat.IntentBuilder(requireContext())
                    .setType("text/plain")
                    .setText(note)
                    .intent
                startActivity(Intent.createChooser(sharingIntent, "Sharing text via"))

//                        val intent = Intent().apply {
//                            action = Intent.ACTION_SEND
//
//                            putExtra(Intent.EXTRA_TEXT, note)
//                            type = "text/plain"
//                        }
//                        val chooser = Intent.createChooser(intent, "Share note via")
//                        startActivity(chooser)
                true
            }

            R.id.edit_delete_action -> {

                args.noteEntity?.let {
                    noteViewModel.deleteNote(it)
                    showSnackbarMsg("Note Deleted")
                    findNavController().navigateUp()
                }
                showSnackbarMsg("Note deleted")
                findNavController().navigateUp()
                true
            }

            else -> false
        }
    }

}