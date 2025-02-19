package com.ilya.myguap.Menu.Logic

import android.content.Context
import com.ilya.myguap.Menu.DataModel.GroupSchedule
import com.ilya.myguap.Menu.DataModel.Schedule
import com.ilya.myguap.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class ScheduleRepository(private val context: Context) {

    fun loadSchedule(): Schedule {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.schedule_data)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)

        val groups = mutableMapOf<String, GroupSchedule>()
        for (groupName in jsonObject.keys()) {
            val groupObject = jsonObject.getJSONObject(groupName)
            val numerator = parseSchedule(groupObject.getJSONArray("Числитель"))
            val denominator = parseSchedule(groupObject.getJSONArray("Знаменатель"))

            groups[groupName] = GroupSchedule(numerator, denominator)
        }

        return Schedule(groups)
    }

    private fun parseSchedule(array: JSONArray): List<List<List<String>>> {
        val result = mutableListOf<List<List<String>>>()
        for (i in 0 until array.length()) {
            val week = array.getJSONArray(i)
            val weekList = mutableListOf<List<String>>()
            for (j in 0 until week.length()) {
                val day = week.getJSONArray(j)
                val dayList = mutableListOf<String>()
                for (k in 0 until day.length()) {
                    dayList.add(day.getString(k))
                }
                weekList.add(dayList)
            }
            result.add(weekList)
        }
        return result
    }
}