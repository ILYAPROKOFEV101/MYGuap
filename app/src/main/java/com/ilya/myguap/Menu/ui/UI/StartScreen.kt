package com.ilya.myguap.Menu.ui.UI


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import com.ilya.reaction.logik.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    viewModel: MyViewModel,
    context: Context,
    uid: String,
    navController: NavController
) {
    var groupNumber by remember { mutableStateOf("") }
    val mygruop = PreferenceHelper.getidgroup(context)
    var groupData by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var showCreateGroupMenu by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val background_color = if (isSystemInDarkTheme()) Color(0xFF191C20)
    else Color(0xFFFFFFFF)
    val text = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFF191C20)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background_color)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Введите название группы",
            style = MaterialTheme.typography.headlineMedium,
            color = text
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Поле ввода для названия группы
        OutlinedTextField(
            value = groupNumber,
            onValueChange = { groupNumber = it },
            label = { Text("Название группы") },
            placeholder = { Text("например, с312") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = text, // Цвет текста при фокусе
                unfocusedTextColor = text, // Цвет текста без фокуса
                focusedLabelColor = text, // Цвет метки при фокусе
                unfocusedLabelColor = text, // Цвет метки без фокуса
                focusedContainerColor = background_color, // Цвет фона при фокусе
                unfocusedContainerColor = background_color, // Цвет фона без фокуса
                focusedIndicatorColor = text, // Цвет рамки при фокусе
                unfocusedIndicatorColor = background_color, // Прозрачная рамка без фокуса
                cursorColor = text, // Цвет курсора
                focusedPlaceholderColor = text, // Цвет плейсхолдера при фокусе
                unfocusedPlaceholderColor = text // Цвет плейсхолдера без фокуса
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Кнопка поиска группы
        Button(
            onClick = {
                if (groupNumber.isEmpty()) {
                    errorMessage = "Пожалуйста, введите название группы."
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        groupData = viewModel.getGroupData(groupNumber)
                        if (groupData == null) {
                            errorMessage = "Группа не найдена. Вы можете создать новую."
                            showCreateGroupMenu = true
                        } else {
                            errorMessage = null
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Поиск группы")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Отображение ошибок
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Отображение информации о группе, если она найдена
        groupData?.let { data ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Такая группа есть",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                Spacer(modifier = Modifier.height(16.dp))
                // Кнопка присоединения к группе
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.addUserToGroup(groupNumber, uid)
                            PreferenceHelper.saveidgroup(context, groupNumber)
                        }
                        navController.navigate("mygroup")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Присоединиться к группе")
                }
                if(mygruop != ""){
                    Button(
                        onClick = {
                            navController.navigate("mygroup")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Вернуться в группу")
                    }
                }


            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Меню для создания группы
        if (showCreateGroupMenu) {
            Creat_Group(
                navController,
                groupNumber,
                viewModel = viewModel,
                context = context,
                uid = uid,
            ) { isCreated ->
                if (isCreated) {
                    Toast.makeText(context, "Группа успешно создана!", Toast.LENGTH_SHORT).show()
                    showCreateGroupMenu = false
                } else {
                    Toast.makeText(context, "Не удалось создать группу.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}