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

import kotlinx.coroutines.launch


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
    fun createGroup(groupNumber: String, creatorUid: String) {
        val ref = database.getReference("groups/$groupNumber")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // Если группа не существует, создаем её
                    val newGroup = mapOf(
                        "админы" to mapOf(
                            "главныйАдмин" to creatorUid,
                            "помощники" to emptyList<String>()
                        ),
                        "users" to listOf(creatorUid),
                        "subjects" to emptyMap<String, Any>()
                    )
                    ref.setValue(newGroup)
                        .addOnSuccessListener {
                            println("Group created successfully")
                        }
                        .addOnFailureListener { error ->
                            println("Failed to create group: ${error.message}")
                        }
                } else {
                    println("Group already exists")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })
    }

    /**
     * Добавление пользователя в группу
     *
     * @param groupNumber - Номер группы
     * @param userUid - UID пользователя
     */
    fun addUserToGroup(groupNumber: String, userUid: String) {
        val ref = database.getReference("groups/$groupNumber/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.getValue<List<String>>() ?: emptyList()
                if (!users.contains(userUid)) {
                    ref.push().setValue(userUid)
                        .addOnSuccessListener {
                            println("User added to group successfully")
                        }
                        .addOnFailureListener { error ->
                            println("Failed to add user to group: ${error.message}")
                        }
                } else {
                    println("User is already in the group")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })
    }

    /**
     * Назначение помощника
     *
     * @param groupNumber - Номер группы
     * @param helperUid - UID помощника
     */
    fun appointHelper(groupNumber: String, helperUid: String) {
        val ref = database.getReference("groups/$groupNumber/админы/помощники")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val helpers = snapshot.getValue<List<String>>() ?: emptyList()
                if (!helpers.contains(helperUid)) {
                    ref.push().setValue(helperUid)
                        .addOnSuccessListener {
                            println("Helper appointed successfully")
                        }
                        .addOnFailureListener { error ->
                            println("Failed to appoint helper: ${error.message}")
                        }
                } else {
                    println("Helper is already appointed")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })
    }

    /**
     * Создание нового предмета
     *
     * @param groupNumber - Номер группы
     * @param subjectName - Название предмета
     */
    fun createSubject(groupNumber: String, subjectName: String) {
        val ref = database.getReference("groups/$groupNumber/subjects/$subjectName")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    ref.setValue(mapOf("labs" to emptyMap<String, Any>(), "homeworks" to emptyMap<String, Any>()))
                        .addOnSuccessListener {
                            println("Subject created successfully")
                        }
                        .addOnFailureListener { error ->
                            println("Failed to create subject: ${error.message}")
                        }
                } else {
                    println("Subject already exists")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })
    }

    /**
     * Создание новой лабораторной работы
     *
     * @param groupNumber - Номер группы
     * @param subjectName - Название предмета
     * @param labName - Название лабораторной работы
     * @param deadline - Дедлайн для лабораторной работы
     */
    fun createLab(groupNumber: String, subjectName: String, labName: String, deadline: String) {
        val ref = database.getReference("groups/$groupNumber/subjects/$subjectName/labs/$labName")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    ref.setValue(mapOf("очередь" to emptyList<String>(), "делайн" to deadline))
                        .addOnSuccessListener {
                            println("Lab created successfully")
                        }
                        .addOnFailureListener { error ->
                            println("Failed to create lab: ${error.message}")
                        }
                } else {
                    println("Lab already exists")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })
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
    fun addHomework(groupNumber: String, subjectName: String, taskName: String, taskDescription: String, deadline: String) {
        val ref = database.getReference("groups/$groupNumber/subjects/$subjectName/homeworks/$taskName")
        ref.setValue(mapOf("описание" to taskDescription, "делайн" to deadline))
            .addOnSuccessListener {
                println("Homework added successfully")
            }
            .addOnFailureListener { error ->
                println("Failed to add homework: ${error.message}")
            }
    }
}