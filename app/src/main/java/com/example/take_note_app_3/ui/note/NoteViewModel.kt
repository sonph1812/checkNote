package com.example.take_note_app_3.ui.note

import androidx.lifecycle.*
import com.example.take_note_app_3.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allNotes: StateFlow<List<NoteWithTodoItems>> = combine(
        repository.allNotesWithTodoItems,
        _searchQuery
    ) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter { noteWithItems ->
                noteWithItems.note.title.contains(query, ignoreCase = true) ||
                        noteWithItems.note.content?.contains(query, ignoreCase = true) == true ||
                        noteWithItems.todoItems.any { it.text.contains(query, ignoreCase = true) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun insertNote(note: Note, todoItems: List<TodoItem> = emptyList()) {
        viewModelScope.launch {
            val noteId = repository.insertNote(note)
            todoItems.forEach {
                repository.insertTodoItem(it.copy(id = 0, noteId = noteId.toInt()))
            }
        }
    }

    fun updateNote(note: Note, todoItems: List<TodoItem> = emptyList()) {
        viewModelScope.launch {
            repository.updateNote(note)
            repository.deleteTodoItemsByNoteId(note.id)
            todoItems.forEach {
                repository.insertTodoItem(it.copy(id = 0, noteId = note.id))
            }
        }
    }

    fun updateTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.updateTodoItem(todoItem)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
