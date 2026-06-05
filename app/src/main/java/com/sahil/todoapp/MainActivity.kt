package com.sahil.todoapp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.combinedClickable
import android.os.Bundle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sahil.todoapp.ui.theme.ToDoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoScreen(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(name: String, modifier: Modifier = Modifier) {


    // This for FAC inside for this function
    var showAddTaskDialog by remember {
        mutableStateOf(false) }


    // This for the add to New task
    val context = LocalContext.current
    val title = rememberTextFieldState()
    val description = rememberTextFieldState()
    val taskList = remember { mutableStateListOf<Task>() }


    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = " ToDo App",
                        fontSize = 22.sp,
//                        fontFamily = FontFamily.Serif,
//                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddTaskDialog = true
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { innerPadding ->
        ScrollContent(innerPadding, taskList)
    }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddTaskDialog = false
            },
            title = {
                Text(text = "Add New Task")
            },
            text = {
                Column {
                    OutlinedTextField(
                        state = title,
                        label = { Text("Add Task Title !") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        state = description,
                        label = { Text("Task Description") }
                    )
                }
            },
            confirmButton = {
                OutlinedButton(onClick = {
                    if (title.text.isBlank() || description.text.isBlank()) {
                        Toast.makeText(
                            context, "Fields cannot be Empty", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val newTask = Task(
                            id = taskList.size + 1,
                            title = title.text.toString(),
                            description = description.text.toString(),
                            isComplete = false
                        )
                        taskList.add(newTask)

                        title.clearText()
                        description.clearText()
                        showAddTaskDialog = false
                    }
                }) {
                    Text(text = "ADD TASK")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAddTaskDialog = false
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoAppTheme {
        TodoScreen("Android")
    }
}

@Composable
fun ScrollContent(innerPadding: PaddingValues, taskList: MutableList<Task>) {
    var showEditDialog by remember { mutableStateOf(false) }
    var selectTask by remember { mutableStateOf<Task?>(null) }

    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
//            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))


        if (taskList.isEmpty()){

            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("empty_notes.json"))
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize()
            )

        }else {


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

            ) {
                items(taskList) { task ->


                    //   CARD OF THE TODO
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)

                            .combinedClickable(
                                onClick = { },
                                onLongClick = {
                                    selectTask = task

                                    editTitle = task.title
                                    editDescription = task.description

                                    showEditDialog = true
                                }
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = task.title,
                                fontSize = 21.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = task.description,
                                fontSize = 14.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold

                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        taskList.remove(task)
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.Red,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Delete")
                                }

                                Spacer(modifier = Modifier.width(14.dp))
                                OutlinedButton(
                                    onClick = {
                                        val index = taskList.indexOf(task)
                                        if (index != -1) {
                                            taskList[index] =
                                                task.copy(isComplete = !task.isComplete)
                                        }
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.Blue,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(text = if (task.isComplete) "Complete" else "Incomplete")
                                }

                            }


                        }
                    }
                }
            }
        }
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = {
                    showEditDialog = false
                },
                title = {
                    Text("Edit Task")
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("Title") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = { Text("Description") }
                        )
                    }
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            val task = selectTask
                            if (task != null) {
                                val index = taskList.indexOf(task)
                                if (index != -1) {
                                    taskList[index] = task.copy(
                                        title = editTitle,
                                        description = editDescription
                                    )
                                }
                            }
                            showEditDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showEditDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

}
