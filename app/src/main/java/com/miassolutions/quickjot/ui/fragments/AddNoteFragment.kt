package com.miassolutions.quickjot.ui.fragments

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.databinding.FragmentAddNoteBinding
import com.miassolutions.quickjot.ui.activities.MainActivity
import com.miassolutions.quickjot.ui.viewmodels.NoteViewModel
import kotlin.getValue


class AddNoteFragment : Fragment(R.layout.fragment_add_note) {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddNoteBinding.bind(view)
        backPressHandler()




    }

    fun backPressHandler(){

        val activity = (activity as MainActivity)

        val toolbar = activity.findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar.setNavigationOnClickListener {
            Log.d("AddNoteFragment", "BackPress handled")
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    Log.d("AddNoteFragment", "BackPress handled")
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