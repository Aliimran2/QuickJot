package com.miassolutions.quickjot.data.repository

import com.miassolutions.quickjot.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun insertNote(noteEntity: NoteEntity)
    suspend fun updateNote(noteEntity: NoteEntity)
    suspend fun deleteNote(noteEntity: NoteEntity)
    fun getNoteById(id: Int) : Flow<NoteEntity?>
    fun getAllNotes() : Flow<List<NoteEntity>>

}