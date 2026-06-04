package com.example.take_note_app_3.ui.note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.take_note_app_3.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    viewModel: NoteViewModel,
    noteId: Int? = null,
    onBack: () -> Unit
) {
    val notes by viewModel.allNotes.collectAsState()
    val focusManager = LocalFocusManager.current
    val existingNoteWithItems = noteId?.let { id -> notes.find { it.note.id == id } }

    var title by remember { mutableStateOf(existingNoteWithItems?.note?.title ?: "") }
    var content by remember { mutableStateOf(existingNoteWithItems?.note?.content ?: "") }
    var noteType by remember { mutableStateOf(existingNoteWithItems?.note?.type ?: NoteType.TEXT) }
    
    // Checklist items state
    val todoItems = remember { 
        mutableStateListOf<TodoItem>().apply {
            if (existingNoteWithItems != null) {
                addAll(existingNoteWithItems.todoItems)
            } else {
                add(TodoItem(noteId = 0, text = ""))
            }
        }
    }

    var showDiscardDialog by remember { mutableStateOf(false) }

    val onBackRequest = {
        if (title.isNotBlank() || content.isNotBlank() || (noteType == NoteType.CHECKLIST && todoItems.any { it.text.isNotBlank() })) {
            showDiscardDialog = true
        } else {
            onBack()
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to discard your unsaved changes?") },
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text("Discard", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep Editing")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "Create Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onBackRequest) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val note = Note(
                            id = noteId ?: 0,
                            title = title,
                            content = if (noteType == NoteType.TEXT) content else null,
                            type = noteType,
                            timestamp = System.currentTimeMillis()
                        )
                        if (noteId == null) {
                            viewModel.insertNote(note, if (noteType == NoteType.CHECKLIST) todoItems.filter { it.text.isNotBlank() } else emptyList())
                        } else {
                            viewModel.updateNote(note, if (noteType == NoteType.CHECKLIST) todoItems.filter { it.text.isNotBlank() } else emptyList())
                        }
                        onBack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Note Title", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NoteTypeTab(
                    selected = noteType == NoteType.TEXT,
                    label = "Text Note",
                    icon = Icons.Default.StickyNote2,
                    modifier = Modifier.weight(1f),
                    onClick = { noteType = NoteType.TEXT }
                )
                NoteTypeTab(
                    selected = noteType == NoteType.CHECKLIST,
                    label = "Checklist",
                    icon = Icons.Default.TaskAlt,
                    modifier = Modifier.weight(1f),
                    onClick = { noteType = NoteType.CHECKLIST }
                )
            }

            if (noteType == NoteType.TEXT) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Write your thoughts here...", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(todoItems) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9F9F9))
                                .padding(horizontal = 8.dp)
                        ) {
                            Checkbox(
                                checked = item.isCompleted,
                                onCheckedChange = { isChecked ->
                                    todoItems[index] = item.copy(isCompleted = isChecked)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            TextField(
                                value = item.text,
                                onValueChange = { newText ->
                                    todoItems[index] = item.copy(text = newText)
                                },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Add task...", fontSize = 14.sp) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = {
                                    if (index == todoItems.size - 1 && item.text.isNotBlank()) {
                                        todoItems.add(TodoItem(noteId = noteId ?: 0, text = ""))
                                    }
                                })
                            )
                            IconButton(onClick = {
                                if (todoItems.size > 1) {
                                    todoItems.removeAt(index)
                                } else {
                                    todoItems[index] = item.copy(text = "")
                                }
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    item {
                        TextButton(
                            onClick = { todoItems.add(TodoItem(noteId = noteId ?: 0, text = "")) },
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Item")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteTypeTab(
    selected: Boolean,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else Color.Gray
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
