package com.sahil.todoapp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.FloatingActionButton
import android.os.Bundle
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room

import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sahil.todoapp.data.AppDatabase
import com.sahil.todoapp.data.TaskEntityDao
import com.sahil.todoapp.data.entity.TaskEntity
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
    var showAddTaskEntityDialog by remember {
        mutableStateOf(false) }


    // This for the add to New TaskEntity
    val context = LocalContext.current
    val title = rememberTextFieldState()


    val db = remember {   //    Connecting UI to the Database
        Room.databaseBuilder(
            context, AppDatabase::class.java,"todo_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val dao=db.taskDao()
    val scope = rememberCoroutineScope()

    val description = rememberTextFieldState()

    val TaskEntityList = remember { mutableStateListOf<TaskEntity>() }

    LaunchedEffect(Unit) {
        val tasks = dao.getALLTaskEntity()

        TaskEntityList.clear()
        TaskEntityList.addAll(tasks)
    }


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
                    showAddTaskEntityDialog = true
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add TaskEntity"
                )
            }
        }
    ) { innerPadding ->
        ScrollContent(innerPadding, TaskEntityList, dao, scope)
    }

    if (showAddTaskEntityDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddTaskEntityDialog = false
            },
            title = {
                Text(text = "Add New TaskEntity")
            },
            text = {
                Column {
                    OutlinedTextField(
                        state = title,
                        label = { Text("Add TaskEntity Title !") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        state = description,
                        label = { Text("TaskEntity Description") }
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
                        val newTaskEntity = TaskEntity(
                            id = TaskEntityList.size + 1,
                            title = title.text.toString(),
                            description = description.text.toString(),
                            isComplete = false
                        )
                        scope.launch {
                            dao.insertTaskEntity(newTaskEntity)
                        }
                        TaskEntityList.add(newTaskEntity)

                        title.clearText()
                        description.clearText()
                        showAddTaskEntityDialog = false
                    }
                }) {
                    Text(text = "ADD Task")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAddTaskEntityDialog = false
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TodoScreenPreview() {
    ToDoAppTheme {
        TodoScreen("Android")
    }
}

@Composable
fun ScrollContent(
    innerPadding: PaddingValues,
    TaskEntityList: MutableList<TaskEntity>,
    dao: TaskEntityDao,
    scope: CoroutineScope
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var selectTaskEntity by remember { mutableStateOf<TaskEntity?>(null) }

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


        if (TaskEntityList.isEmpty()){

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
                items(TaskEntityList) { TaskEntity ->




                    TaskEntityCard(
                        task = TaskEntity,
                        onDelete = {

                            scope.launch {
                                dao.deleteTaskEntity(TaskEntity)
                            }
                            TaskEntityList.remove(TaskEntity)
                        },
                        onStatusChange = {
                            val index = TaskEntityList.indexOf(TaskEntity)
                            if (index != -1) {
                                val updatedTask = TaskEntity.copy(isComplete = !TaskEntity.isComplete)
                                TaskEntityList[index] = updatedTask
                                scope.launch {
                                    dao.updateTaskEntity(updatedTask)
                                }
                            }
                        },
                        onLongClick = {
                            selectTaskEntity = TaskEntity
                            editTitle = TaskEntity.title
                            editDescription = TaskEntity.description
                            showEditDialog = true
                        }
                    )

                }
            }
        }
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = {
                    showEditDialog = false
                },
                title = {
                    Text("Edit TaskEntity")
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
                            val currentTask = selectTaskEntity
                            if (currentTask != null) {
                                val index = TaskEntityList.indexOf(currentTask)
                                if (index != -1) {
                                    val updatedTask = currentTask.copy(
                                        title = editTitle,
                                        description = editDescription
                                    )
                                    TaskEntityList[index] = updatedTask
                                    scope.launch {
                                        dao.updateTaskEntity(updatedTask)
                                    }
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
