package com.miassolutions.quickjot.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.miassolutions.quickjot.data.local.NoteEntity

class NoteDiffCallback : DiffUtil.ItemCallback<NoteEntity>() {
    override fun areItemsTheSame(
        oldItem: NoteEntity,
        newItem: NoteEntity,
    ): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(
        oldItem: NoteEntity,
        newItem: NoteEntity,
    ): Boolean {
        return oldItem == newItem
    }
}