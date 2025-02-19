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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.ilya.myguap.Menu.DataModel.GroupSchedule
import com.ilya.myguap.Menu.Logic.MyViewModel
import com.ilya.myguap.Menu.Logic.ScheduleViewModel


@Composable
fun GroupScheduleScreen(viewModel: ScheduleViewModel) {
    var groupName by remember { mutableStateOf("") } // Состояние для ввода номера группы
    var groupSchedule by remember { mutableStateOf<GroupSchedule?>(null) } // Состояние для расписания

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Поле ввода номера группы
        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Введите номер группы") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для поиска расписания
        Button(onClick = {
            groupSchedule = viewModel.getGroupSchedule(groupName)
        }) {
            Text("Найти расписание")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Отображение расписания
        if (groupSchedule != null) {
            Text(
                text = "Расписание для группы $groupName",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Числитель:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ScheduleTable(schedule = groupSchedule!!.numerator)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Знаменатель:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ScheduleTable(schedule = groupSchedule!!.denominator)
        } else if (groupName.isNotEmpty()) {
            Text("Расписание для группы $groupName не найдено")
        }
    }
}

@Composable
fun ScheduleTable(schedule: List<List<List<String>>>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        schedule.forEachIndexed { weekIndex, week ->
            item {
                Text(
                    text = "Неделя ${weekIndex + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            week.forEach { day ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Заголовок дня недели
                        Text(
                            text = day.first(),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Список занятий
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        ) {
                            day.drop(1).forEach { lesson ->
                                Text(
                                    text = lesson,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}