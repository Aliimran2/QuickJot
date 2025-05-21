package com.miassolutions.quickjot.data.repository

import com.miassolutions.quickjot.data.local.NoteDao
import com.miassolutions.quickjot.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImp @Inject constructor(private val dao: NoteDao) : NoteRepository {
    override suspend fun insertNote(noteEntity: NoteEntity) {
        dao.insertNote(noteEntity)
    }

    override suspend fun updateNote(noteEntity: NoteEntity) {
        dao.updateNote(noteEntity)
    }

    override suspend fun deleteNote(noteEntity: NoteEntity) {
        dao.deleteNote(noteEntity)
    }

    override fun getNoteById(id: Int): Flow<NoteEntity?> {
        return dao.getNoteById(id)
    }

    override fun getAllNotes(): Flow<List<NoteEntity>> {
        return dao.getAllNotes()
    }
}