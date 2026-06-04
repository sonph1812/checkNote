package com.example.take_note_app_3.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotesWithTodoItems: Flow<List<NoteWithTodoItems>> = noteDao.getNotesWithTodoItems()

    fun getNoteWithTodoItemsById(id: Int): Flow<NoteWithTodoItems?> = noteDao.getNoteWithTodoItemsById(id)

    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun insertTodoItem(todoItem: TodoItem) = noteDao.insertTodoItem(todoItem)

    suspend fun updateTodoItem(todoItem: TodoItem) = noteDao.updateTodoItem(todoItem)

    suspend fun deleteTodoItem(todoItem: TodoItem) = noteDao.deleteTodoItem(todoItem)

    suspend fun deleteTodoItemsByNoteId(noteId: Int) = noteDao.deleteTodoItemsByNoteId(noteId)
}
