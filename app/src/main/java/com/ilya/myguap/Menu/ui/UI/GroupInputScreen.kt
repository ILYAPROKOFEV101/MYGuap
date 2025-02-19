package com.ilya.myguap.Menu.ui.UI


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ilya.myguap.Menu.Logic.MyViewModel


@Composable
fun GroupInputScreen(
    viewModel: MyViewModel,
    modifier: Modifier,
    context: Context
    ) {
        var groupNumber by remember { mutableStateOf("") }
        var subjectName by remember { mutableStateOf("") }
        var labNumber by remember { mutableStateOf("") }
        var studentName by remember { mutableStateOf("") }
        var taskName by remember { mutableStateOf("") }
        var taskDescription by remember { mutableStateOf("") }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Manage Group Data",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = groupNumber,
                onValueChange = { groupNumber = it },
                label = { Text("Group Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Subject Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = labNumber,
                onValueChange = { labNumber = it },
                label = { Text("Lab Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("Student Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    if (groupNumber.isNotEmpty() && subjectName.isNotEmpty() && labNumber.isNotEmpty() && studentName.isNotEmpty()) {

                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text("Add Student")
                }

                Button(onClick = {
                    if (groupNumber.isNotEmpty() && subjectName.isNotEmpty() && labNumber.isNotEmpty() && studentName.isNotEmpty()) {

                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text("Remove Student")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Task Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    if (groupNumber.isNotEmpty() && taskName.isNotEmpty() && taskDescription.isNotEmpty()) {

                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text("Add Homework")
                }

                Button(onClick = {
                    if (groupNumber.isNotEmpty() && taskName.isNotEmpty()) {

                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text("Remove Homework")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Saved Data:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            val savedData by viewModel.data.collectAsState()

            LazyColumn {
                items(savedData.toList()) { (key, value) ->
                    Text(text = "$key: $value", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }