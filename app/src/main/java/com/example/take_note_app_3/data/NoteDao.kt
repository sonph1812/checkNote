package com.example.take_note_app_3.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note): Unit

    @Delete
    suspend fun deleteNote(note: Note): Unit

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Transaction
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getNotesWithTodoItems(): Flow<List<NoteWithTodoItems>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteWithTodoItemsById(id: Int): Flow<NoteWithTodoItems?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(todoItem: TodoItem): Unit

    @Update
    suspend fun updateTodoItem(todoItem: TodoItem): Unit

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem): Unit

    @Query("DELETE FROM todo_items WHERE noteId = :noteId")
    suspend fun deleteTodoItemsByNoteId(noteId: Int): Unit
}
