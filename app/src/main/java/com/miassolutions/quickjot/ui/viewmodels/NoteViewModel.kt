package com.miassolutions.quickjot.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.quickjot.data.local.NoteEntity
import com.miassolutions.quickjot.data.repository.RepositoryImp
import com.miassolutions.quickjot.utils.PreferenceManager
import com.miassolutions.quickjot.utils.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: RepositoryImp,
    private val prefs: PreferenceManager,
) : ViewModel() {

    private val _allNotes = repository.getAllNotes()

    private val _sortedNotes = MutableStateFlow<SortOrder>(prefs.getSortType())
    val sortOrder = _sortedNotes.asStateFlow()

    fun sortOrder(sortOrder: SortOrder) {
        _sortedNotes.value = sortOrder
        prefs.saveSortOrder(sortOrder)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notesSorted: StateFlow<List<NoteEntity>> = sortOrder
        .flatMapLatest { order ->
            repository.getSortedNotes(order)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val searchQuery = MutableStateFlow<String>("")

    fun updateQuery(query: String) {
        searchQuery.value = query
    }

    val notes: Flow<List<NoteEntity>> = combine(notesSorted, searchQuery) { notesSorted, query ->
        if (query.trim().isBlank()) notesSorted
        else notesSorted.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
        }

    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertAgain(noteEntity: NoteEntity) = viewModelScope.launch {
        repository.insertNote(noteEntity)
    }


    fun insertNote(title: String, content: String) {
        val note = NoteEntity(
            title = title,
            content = content,
        )
        Log.d("NoteViewModel", "Inserting note: $title")
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