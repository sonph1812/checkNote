package com.example.take_note_app_3.data

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithTodoItems(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val todoItems: List<TodoItem>
)
