package com.ilya.myguap.Menu.ui.UI


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ilya.myguap.Menu.DataModel.GroupData
import com.ilya.myguap.Menu.Logic.MyViewModel
import com.ilya.reaction.logik.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun GetGroupDataScreen(
    viewModel: MyViewModel,
    context: Context,
    currentUser: String
) {
    // Получаем номер группы из настроек
    val groupNumber = PreferenceHelper.getidgroup(context)
    var groupData by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Флаг загрузки
    var isError by remember { mutableStateOf(false) } // Флаг ошибки
    var isAdmin by remember { mutableStateOf(false) } // Является ли пользователь админом
    var homeworks by remember { mutableStateOf<List<Pair<String, String>>?>(null) } // Домашние задания (название + описание)

    // Запускаем корутину с помощью LaunchedEffect
    LaunchedEffect(key1 = groupNumber) {
        try {
            // Получаем данные группы
            groupData = viewModel.getGroupData(groupNumber.toString())
            if (groupData != null) {
                // Проверяем, является ли пользователь администратором
                val admins = groupData?.get("админы") as? Map<*, *>
                isAdmin = (admins?.get("главныйАдмин") == currentUser || (admins?.get("помощники") as? List<*>)?.contains(currentUser) == true)
                // Получаем список домашних заданий
                homeworks = groupData?.get("homeworks") as? List<Pair<String, String>>
            } else {
                isError = true
            }
        } catch (e: Exception) {
            isError = true
        } finally {
            isLoading = false // Загрузка завершена
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Group Data",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Показываем индикатор загрузки
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (isError) {
            // Показываем сообщение об ошибке
            Text(
                text = "Failed to load group data",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            // Отображаем данные группы
            groupData?.let { data ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Главный админ: ${data["админы"]?.let { (it as? Map<*, *>)?.get("главныйАдмин") }}")
                    Text("Помощники: ${data["админы"]?.let { (it as? Map<*, *>)?.get("помощники") ?: "None" }}")
                    Text("Users: ${(data["users"] as? List<*>)?.joinToString(", ") ?: "None"}")
                    Text("Google Sheet Link: ${data["googletabel"]}")
                    Text("Community Link: ${data["communityLink"]}")
                    Text("Navigation: ${data["navigation"]}")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Если пользователь админ, показываем панель администратора
                    if (isAdmin) {
                        AdminPanelForHomeworks(
                            viewModel = viewModel,
                            groupNumber = groupNumber.toString(),
                            onAddHomework = { homeworkName, description ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.addHomeWork(groupNumber.toString(), homeworkName, description)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    // Просмотр списка домашних заданий
                    Text(
                        text = "Homeworks:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    homeworks?.forEachIndexed { index, (name, description) ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "${index + 1}. $name", style = MaterialTheme.typography.titleSmall)
                            Text(text = "Description: $description", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

// Панель администратора для добавления домашних заданий
@Composable
fun AdminPanelForHomeworks(
    viewModel: MyViewModel,
    groupNumber: String,
    onAddHomework: (String, String) -> Unit
) {
    var newHomeworkName by remember { mutableStateOf("") }
    var newHomeworkDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Admin Panel for Homeworks",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Поле для ввода названия домашнего задания
        OutlinedTextField(
            value = newHomeworkName,
            onValueChange = { newHomeworkName = it },
            label = { Text("Homework Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Поле для ввода описания домашнего задания
        OutlinedTextField(
            value = newHomeworkDescription,
            onValueChange = { newHomeworkDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Кнопка для добавления домашнего задания
        Button(
            onClick = {
                if (newHomeworkName.isNotEmpty() && newHomeworkDescription.isNotEmpty()) {
                    onAddHomework(newHomeworkName, newHomeworkDescription)
                    newHomeworkName = ""
                    newHomeworkDescription = ""
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = newHomeworkName.isNotEmpty() && newHomeworkDescription.isNotEmpty()
        ) {
            Text("Add Homework")
        }
    }
}