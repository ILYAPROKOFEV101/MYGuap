package com.ilya.myguap.Menu.ui.UI


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.style.TextAlign
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
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF191C20) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
            .padding(16.dp)

        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            colors = ButtonDefaults.buttonColors(Color(0xFF03A9F4)),
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))

                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Расписание для группы $groupName",
                    style = TextStyle(fontSize = 20.sp),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                    tint = textColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }



            groupSchedule = viewModel.getGroupSchedule(groupName)


        Spacer(modifier = Modifier.height(16.dp))



        // Отображение расписания
        if (groupSchedule != null) {

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

    // Определяем текущую тему (темная или светлая)
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF191C20) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF2B2E33) else Color(0xFFF5F5F5)

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
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor), // Устанавливаем фоновый цвет в зависимости от темы
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
                style = MaterialTheme.typography.titleMedium.copy(color = textColor), // Цвет текста зависит от темы
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    isEvenSunday = !isEvenSunday // Переключаем состояние
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = cardColor, // Цвет кнопки зависит от темы
                    contentColor = textColor // Цвет текста на кнопке
                ),
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
                            .background(cardColor) // Цвет карточки зависит от темы
                            .padding(8.dp)
                    ) {
                        // Заголовок дня недели
                        Text(
                            text = day.first(),
                            style = MaterialTheme.typography.titleSmall.copy(color = textColor), // Цвет текста зависит от темы
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
                                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor), // Цвет текста зависит от темы
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}