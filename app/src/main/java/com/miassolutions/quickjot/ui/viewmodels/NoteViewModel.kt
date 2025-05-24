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


    private val _currentSortOrder = MutableStateFlow<SortOrder>(prefs.getSortType())
    val currentSortOrder = _currentSortOrder.asStateFlow()

    fun sortOrder(sortOrder: SortOrder) {
        _currentSortOrder.value = sortOrder
        prefs.saveSortOrder(sortOrder)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val sortedNotes: StateFlow<List<NoteEntity>> = currentSortOrder
        .flatMapLatest { order ->
            repository.getSortedNotes(order)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val searchQuery = MutableStateFlow<String>("")

    fun updateQuery(query: String) {
        searchQuery.value = query
    }

    val displayedNotes: Flow<List<NoteEntity>> = combine(sortedNotes, searchQuery) { notes, query ->
        if (query.trim().isBlank()) notes
        else notes.filter {
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


    //multi selection logic

    private val _selectedNoteIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedNoteIds = _selectedNoteIds.asStateFlow()

    // This will hold the complete list of items
    private val _allAvailableNotes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val allAvailableNotes: StateFlow<List<NoteEntity>> = _allAvailableNotes.asStateFlow()


    init {
        viewModelScope.launch {
            sortedNotes.collect {
                _allAvailableNotes.value = it
            }
        }
    }

    fun toggleSelection(itemId: Int) {
        _selectedNoteIds.value = if (_selectedNoteIds.value.contains(itemId)) {
            _selectedNoteIds.value - itemId
        } else {
            _selectedNoteIds.value + itemId
        }
    }

    fun selectAll() {
        _selectedNoteIds.value = _allAvailableNotes.value.map { it.noteId }.toSet()
    }

    fun clearSelection() {
        _selectedNoteIds.value = emptySet<Int>()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            repository.deleteNotesByIds(_selectedNoteIds.value.toList())
            clearSelection()
        }
    }


}

