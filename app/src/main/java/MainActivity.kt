package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ToDoApp() }
    }
}

@Composable
fun ToDoApp() {
    var items by rememberSaveable(stateSaver = listSaver(
        save = { state -> state.map { listOf(it.id, it.label, it.isCompleted) } },
        restore = { restored ->
            restored.map {
                ToDoItem(it[0] as Long, it[1] as String, it[2] as Boolean)
            }.toMutableList()
        }
    )) { mutableStateOf(mutableListOf<ToDoItem>()) }

    Surface {
        ToDoScreen(
            items = items,
            onAddItem = { label ->
                if (label.isNotBlank()) {
                    items = (items + ToDoItem(System.currentTimeMillis(), label.trim())).toMutableList()
                }
            },
            onCheckChange = { id, isCompleted ->
                items = items.map { if (it.id == id) it.copy(isCompleted = isCompleted) else it }.toMutableList()
            },
            onDelete = { id ->
                items = items.filter { it.id != id }.toMutableList()
            }
        )
    }
}

@Composable
fun ToDoScreen(
    items: List<ToDoItem>,
    onAddItem: (String) -> Unit,
    onCheckChange: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    val activeItems = items.filter { !it.isCompleted }
    val completedItems = items.filter { it.isCompleted }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    errorText = ""
                },
                label = { Text("New Item") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (text.trim().isBlank()) {
                    errorText = "Cannot be blank!"
                } else {
                    onAddItem(text)
                    text = ""
                }
            }) {
                Text("Add")
            }
        }
        if (errorText.isNotEmpty()) {
            Text(errorText, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            if (activeItems.isNotEmpty()) {
                item {
                    Text("Items", style = MaterialTheme.typography.headlineSmall)
                }
                items(activeItems) { item ->
                    ToDoRow(item, onCheckChange, onDelete)
                }
            }

            if (completedItems.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Completed Items", style = MaterialTheme.typography.headlineSmall)
                }
                items(completedItems) { item ->
                    ToDoRow(item, onCheckChange, onDelete)
                }
            }
        }
    }
}

@Composable
fun ToDoRow(
    item: ToDoItem,
    onCheckChange: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = { checked -> onCheckChange(item.id, checked) }
        )
        Text(item.label, modifier = Modifier.weight(1f))
        IconButton(onClick = { onDelete(item.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}