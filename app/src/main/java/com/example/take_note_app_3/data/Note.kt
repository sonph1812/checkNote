package com.example.take_note_app_3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NoteType {
    TEXT, CHECKLIST
}

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String? = null,
    val type: NoteType = NoteType.TEXT,
    val timestamp: Long = System.currentTimeMillis()
)
