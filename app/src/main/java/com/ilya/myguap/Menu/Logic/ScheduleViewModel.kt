package com.ilya.myguap.Menu.Logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilya.myguap.Menu.DataModel.GroupSchedule
import com.ilya.myguap.Menu.DataModel.Schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {

    private val _scheduleData = MutableStateFlow<Schedule?>(null)
    val scheduleData: StateFlow<Schedule?> = _scheduleData

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            val data = repository.loadSchedule()
            _scheduleData.value = data
        }
    }

    // Метод для поиска расписания с нормализацией ввода
    fun getGroupSchedule(groupName: String): GroupSchedule? {
        val normalizedGroupName = normalizeGroupName(groupName)
        return scheduleData.value?.groups?.get(normalizedGroupName)
    }

    private fun normalizeGroupName(input: String): String {
        var groupName = input.uppercase() // Приводим к верхнему регистру
        groupName = groupName.replace("C", "С") // Латинская "C" -> Кириллическая "С"
        groupName = groupName.replace("-", "") // Удаляем дефисы
        groupName = groupName.replace(" ", "") // Удаляем пробелы

        // Если ввод содержит только цифры, добавляем префикс "С"
        if (groupName.matches(Regex("\\d+"))) { // Проверяем, состоит ли строка только из цифр
            groupName = "С$groupName" // Добавляем префикс "С"
        }

        return groupName
    }
}