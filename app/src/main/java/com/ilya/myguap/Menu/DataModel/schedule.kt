package com.ilya.myguap.Menu.DataModel

data class Schedule(
    val groups: Map<String, GroupSchedule>
)

data class GroupSchedule(
    val numerator: List<List<List<String>>>, // Числитель
    val denominator: List<List<List<String>>> // Знаменатель
)