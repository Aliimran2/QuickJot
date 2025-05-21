package com.miassolutions.quickjot.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miassolutions.quickjot.R
import com.miassolutions.quickjot.databinding.FragmentNoteListBinding


class NoteListFragment : Fragment(R.layout.fragment_note_list) {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteListBinding.bind(view)






    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}