package com.ilya.myguap.Menu.ui.UI


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
    context: Context
) {
    // Получаем номер группы из настроек
    val groupNumber = PreferenceHelper.getidgroup(context)
    var groupData by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Флаг загрузки
    var isError by remember { mutableStateOf(false) } // Флаг ошибки

    // Запускаем корутину с помощью LaunchedEffect
    LaunchedEffect(key1 = groupNumber) {
        try {
            // Получаем данные группы
            groupData = viewModel.getGroupData(groupNumber.toString())
            if (groupData == null) {
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
            text = "Enter Group Number",
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
                Column {
                    Text("Главный админ: ${data["админы"]?.let { (it as? Map<*, *>)?.get("главныйАдмин") }}")
                    Text("Помощники: ${data["админы"]?.let { (it as? Map<*, *>)?.get("помощники") ?: "None" }}")
                    Text("Users: ${(data["users"] as? List<*>)?.joinToString(", ") ?: "None"}")
                    Text("Google Sheet Link: ${data["googletabel"]}")
                    Text("Community Link: ${data["communityLink"]}")
                    Text("Navigation: ${data["navigation"]}")
                }
            }
        }
    }
}