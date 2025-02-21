package com.ilya.myguap.Menu.ui.UI


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ilya.myguap.Menu.DataModel.GroupSchedule
import com.ilya.myguap.Menu.Logic.MyViewModel
import com.ilya.myguap.Menu.Logic.ScheduleViewModel
import com.ilya.myguap.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


@Composable
fun GroupScheduleScreen(viewModel: ScheduleViewModel, groupName: String) {

    var groupSchedule by remember { mutableStateOf<GroupSchedule?>(null) } // Состояние для расписания

    var isExpanded by remember { mutableStateOf(false) }
    val size by animateDpAsState(targetValue = if (isExpanded) 700.dp else 85.dp)

    // Состояние для отслеживания времени последнего клика
    var lastClickTime by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime < 300) { // Проверяем интервал между кликами
                        isExpanded = !isExpanded // Переключаем состояние при двойном клике
                    }
                    lastClickTime = currentTime // Обновляем время последнего клика
                })
            }
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



            groupSchedule = viewModel.getGroupSchedule(groupName)


        Spacer(modifier = Modifier.height(16.dp))

        // Отображение расписания
        if (groupSchedule != null) {
            Text(
                text = "Расписание для группы $groupName",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ScheduleTable(schedule = groupSchedule!!.numerator)

            Spacer(modifier = Modifier.height(16.dp))

        } else if (groupName.isNotEmpty()) {
            Text("Расписание для группы $groupName не найдено")
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun ScheduleTable(schedule: List<List<List<String>>>) {
    Log.d("ScheduleTable", "Schedule: $schedule")

    // Определяем текущий день недели
    val currentDayOfWeek = LocalDate.now().dayOfWeek
    val daysOfWeek = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )
    val currentDayIndex = daysOfWeek.indexOf(currentDayOfWeek)

    // Определяем дату ближайшего воскресенья
    val today = LocalDate.now()
    val nextSunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    var isEvenSunday by remember { mutableStateOf(nextSunday.dayOfMonth % 2 == 0) }

    // Определяем, какое расписание показывать (числитель или знаменатель)
    val weekToDisplay = if (isEvenSunday) schedule[0] else schedule[1]
    val weekText = if (isEvenSunday) "Числитель" else "Знаменатель"

    // Время пар
    val lessonTimes = listOf(
        "9:20 - 10:55",
        "11:05 - 12:40",
        "13:20 - 14:55",
        "15:05 - 16:40",
        "17:20 - 18:55"
    )

    // Создаем LazyListState для управления прокруткой
    val listState = rememberLazyListState()

    // Прокручиваем до текущего дня при первом рендере
    LaunchedEffect(Unit) {
        if (currentDayIndex != -1) {
            listState.scrollToItem(currentDayIndex)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок числителя/знаменателя
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = weekText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    isEvenSunday = !isEvenSunday // Переключаем состояние
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = if (isEvenSunday) "Показать знаменатель" else "Показать числитель",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Отображаем только одну колонку с расписанием
        LazyColumn(
            state = listState, // Подключаем LazyListState
            modifier = Modifier.fillMaxHeight()
        ) {
            weekToDisplay.forEachIndexed { dayIndex, day ->
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
                            day.drop(1).forEachIndexed { lessonIndex, lesson ->
                                val time = lessonTimes.getOrNull(lessonIndex) ?: "Время не указано"
                                Text(
                                    text = "$time: $lesson",
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