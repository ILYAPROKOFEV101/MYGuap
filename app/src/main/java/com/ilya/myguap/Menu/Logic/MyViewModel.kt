package com.ilya.myguap.Menu.Logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.ilya.myguap.Menu.DataModel.GroupData

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MyViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance("https://myguapapp-default-rtdb.europe-west1.firebasedatabase.app/")
    private val _data = MutableStateFlow<Map<String, Any>>(emptyMap())
    val data: StateFlow<Map<String, Any>> = _data

    /**
     * Создание новой группы
     *
     * @param groupNumber - Номер группы
     * @param creatorUid - UID создателя группы
     */



    suspend fun createGroup(
        groupNumber: String,
        creatorUid: String,
        googleSheetLink: String,
        navigation: String,
        communityLink: String
    ): Boolean {
        return try {
            // Нормализуем имя группы (оставляем только цифры и добавляем префикс "С")
            val normalizedGroupName = normalizeGroupName(groupNumber)

            // Проверяем, что нормализованное имя не пустое
            if (normalizedGroupName.isEmpty()) {
                return false // Если цифр нет, группа не может быть создана
            }

            // Получаем ссылку на группу в базе данных
            val ref = database.getReference("groups/$normalizedGroupName")
            val snapshot = ref.get().await() // Асинхронно получаем данные

            if (!snapshot.exists()) {
                // Если группа не существует, создаем её
                val newGroup = mapOf(
                    "админы" to mapOf(
                        "главныйАдмин" to creatorUid,
                        "помощники" to emptyList<String>()
                    ),
                    "users" to listOf(creatorUid),
                    "subjects" to emptyMap<String, Any>(),
                    "googletabel" to googleSheetLink,
                    "communityLink" to communityLink,
                    "navigation" to navigation
                )
                ref.setValue(newGroup).await() // Асинхронно создаем группу
                true // Успешно создано
            } else {
                false // Группа уже существует
            }
        } catch (e: Exception) {
            false // Ошибка при создании
        }
    }





    suspend fun getGroupData(groupNumber: String): Map<String, Any?>? {
        val normalizedGroupName = GroupNameNormalizer.normalize(groupNumber)
        if (normalizedGroupName.isEmpty()) return null

        val ref = database.getReference("groups/$normalizedGroupName")
        val snapshot = ref.get().await()
        return if (snapshot.exists()) {
            snapshot.value as? Map<String, Any?>
        } else {
            null
        }
    }

    /**
     * Добавление пользователя в группу
     *
     * @param groupNumber - Номер группы
     * @param userUid - UID пользователя
     */
    suspend fun addUserToGroup(groupNumber: String, userUid: String): Boolean {
        val normalizedGroupName = GroupNameNormalizer.normalize(groupNumber)
        if (normalizedGroupName.isEmpty()) return false

        val ref = database.getReference("groups/$normalizedGroupName/users")
        val snapshot = ref.get().await()
        val users = snapshot.getValue<List<String>>() ?: emptyList()

        if (!users.contains(userUid)) {
            ref.push().setValue(userUid).await()
            return true
        }
        return false
    }

    /**
     * Назначение помощника
     *
     * @param groupNumber - Номер группы
     * @param helperUid - UID помощника
     */
    suspend fun appointHelper(groupNumber: String, helperUid: String): Boolean {
        val normalizedGroupName = GroupNameNormalizer.normalize(groupNumber)
        if (normalizedGroupName.isEmpty()) return false

        val ref = database.getReference("groups/$normalizedGroupName/админы/помощники")
        val snapshot = ref.get().await()
        val helpers = snapshot.getValue<List<String>>() ?: emptyList()

        if (!helpers.contains(helperUid)) {
            ref.push().setValue(helperUid).await()
            return true
        }
        return false
    }

    /**
     * Создание нового предмета
     *
     * @param groupNumber - Номер группы
     * @param subjectName - Название предмета
     */
    suspend fun createSubject(groupNumber: String, subjectName: String): Boolean {
        val normalizedGroupName = GroupNameNormalizer.normalize(groupNumber)
        if (normalizedGroupName.isEmpty()) return false

        val ref = database.getReference("groups/$normalizedGroupName/subjects/$subjectName")
        val snapshot = ref.get().await()
        if (!snapshot.exists()) {
            ref.setValue(mapOf("labs" to emptyMap<String, Any>(), "homeworks" to emptyMap<String, Any>())).await()
            return true
        }
        return false
    }

    /**
     * Создание новой лабораторной работы
     *
     * @param groupNumber - Номер группы
     * @param subjectName - Название предмета
     * @param labName - Название лабораторной работы
     * @param deadline - Дедлайн для лабораторной работы
     */
    suspend fun createLab(groupNumber: String, subjectName: String, labName: String, deadline: String): Boolean {
        val normalizedGroupName = GroupNameNormalizer.normalize(groupNumber)
        if (normalizedGroupName.isEmpty()) return false

        val ref = database.getReference("groups/$groupNumber/subjects/$subjectName/labs/$labName")
        val snapshot = ref.get().await()
        if (!snapshot.exists()) {
            ref.setValue(mapOf("очередь" to emptyList<String>(), "делайн" to deadline)).await()
            return true
        }
        return false
    }

    /**
     * Добавление домашнего задания
     *
     * @param groupNumber - Номер группы
     * @param subjectName - Название предмета
     * @param taskName - Название задания
     * @param taskDescription - Описание задания
     * @param deadline - Дедлайн для задания
     */
    suspend fun addHomework(
        groupNumber: String,
        subjectName: String,
        taskName: String,
        taskDescription: String,
        deadline: String
    ): Boolean {
        val normalizedGroupName = GroupNameNormalizer.normalize(groupNumber)
        if (normalizedGroupName.isEmpty()) return false

        val ref = database.getReference("groups/$groupNumber/subjects/$subjectName/homeworks/$taskName")
        ref.setValue(mapOf("описание" to taskDescription, "делайн" to deadline)).await()
        return true
    }


    private fun normalizeGroupName(input: String): String {
        // Извлекаем только цифры из строки
        val digits = input.filter { it.isDigit() }
        // Если строка содержит только цифры, добавляем префикс "С"
        return if (digits.isNotEmpty()) {
            "С$digits" // Добавляем префикс "С"
        } else {
            "" // Если цифр нет, возвращаем пустую строку
        }
    }
}