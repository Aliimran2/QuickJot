package com.miassolutions.quickjot.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.quickjot.data.local.NoteEntity
import com.miassolutions.quickjot.data.repository.RepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: RepositoryImp) : ViewModel() {

    private val _allNotes = repository.getAllNotes()

    private val searchQuery = MutableStateFlow<String>("")

    fun updateQuery(query: String) {
        searchQuery.value = query
    }

    val notes: Flow<List<NoteEntity>> = combine(_allNotes, searchQuery) { allNotes, query ->
        if (query.trim().isBlank()) allNotes
        else allNotes.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
        }

    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun insertNote(title: String, content: String) = {
        val note = NoteEntity(
            title = title,
            content = content,
        )
        viewModelScope.launch(Dispatchers.IO) { repository.insertNote(note) }
    }

    fun updateNote(noteEntity: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(noteEntity.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(noteEntity: NoteEntity) =
        viewModelScope.launch { repository.deleteNote(noteEntity) }



}