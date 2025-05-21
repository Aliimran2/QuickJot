package com.miassolutions.quickjot.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Query("SELECT * FROM note_table WHERE noteId= :id LIMIT 1")
    fun getNoteById(id: Int): Flow<NoteEntity?>

    @Query("SELECT * FROM note_table ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>
}