package com.example.take_note_app_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.take_note_app_3.data.NoteDatabase
import com.example.take_note_app_3.data.NoteRepository
import com.example.take_note_app_3.ui.note.NoteEditScreen
import com.example.take_note_app_3.ui.note.NoteListScreen
import com.example.take_note_app_3.ui.note.NoteViewModel
import com.example.take_note_app_3.ui.note.NoteViewModelFactory
import com.example.take_note_app_3.ui.theme.Takenoteapp3Theme

class MainActivity : ComponentActivity() {
    private val database by lazy { NoteDatabase.getDatabase(this) }
    private val repository by lazy { NoteRepository(database.noteDao()) }
    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Takenoteapp3Theme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }
                var editingNoteId by remember { mutableStateOf<Int?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    when (currentScreen) {
                        is Screen.List -> {
                            NoteListScreen(
                                viewModel = viewModel,
                                onNoteClick = { id ->
                                    editingNoteId = id
                                    currentScreen = Screen.Edit
                                },
                                onAddNoteClick = {
                                    editingNoteId = null
                                    currentScreen = Screen.Edit
                                }
                            )
                        }
                        is Screen.Edit -> {
                            NoteEditScreen(
                                viewModel = viewModel,
                                noteId = editingNoteId,
                                onBack = {
                                    currentScreen = Screen.List
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object List : Screen()
    object Edit : Screen()
}
