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

class MyViewModel : ViewModel() {

    private val _data = MutableStateFlow<Map<String, Any>>(emptyMap())
    var data: StateFlow<Map<String, Any>> = _data


    private val _searchResult = MutableStateFlow<String?>(null)
    val searchResult: StateFlow<String?> = _searchResult



    private val database = FirebaseDatabase.getInstance("https://myguapapp-default-rtdb.europe-west1.firebasedatabase.app/")

    init {
        fetchDataFromFirebase()
    }


    fun searchGroupByNumber(groupNumber: String) {
        val ref = database.getReference("groups/$groupNumber")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val groupData = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    val result = groupData?.entries?.joinToString { "${it.key}: ${it.value}" } ?: "No data found"
                    _searchResult.value = result
                    data = MutableStateFlow(groupData ?: emptyMap())
                } else {
                    _searchResult.value = "Group not found"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _searchResult.value = "Error: ${error.message}"
            }
        })
    }


    private fun fetchDataFromFirebase() {
        val ref = database.getReference("groups")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val groupMap = snapshot.getValue(object : GenericTypeIndicator<HashMap<String, Any>>() {})
                    viewModelScope.launch {
                        _data.value = groupMap ?: emptyMap()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })
    }

    fun saveGroupData(groupNumber: String, groupData: Map<String, Any>) {
        val ref = database.getReference("groups/$groupNumber")
        ref.setValue(groupData)
            .addOnSuccessListener {
                println("Group data saved successfully")
            }
            .addOnFailureListener { error ->
                println("Failed to save group data: ${error.message}")
            }
    }

    fun addStudentToLab(groupNumber: String, subjectName: String, labNumber: String, studentName: String) {
        val ref = database.getReference("groups/$groupNumber/subjects/$subjectName/labs/$labNumber")
        ref.child(studentName).setValue(true)
            .addOnSuccessListener {
                println("Student added successfully")
            }
            .addOnFailureListener { error ->
                println("Failed to add student: ${error.message}")
            }
    }

    fun removeStudentFromLab(groupNumber: String, subjectName: String, labNumber: String, studentName: String) {
        val ref = database.getReference("groups/$groupNumber/subjects/$subjectName/labs/$labNumber/$studentName")
        ref.removeValue()
            .addOnSuccessListener {
                println("Student removed successfully")
            }
            .addOnFailureListener { error ->
                println("Failed to remove student: ${error.message}")
            }
    }

    fun addHomework(groupNumber: String, taskName: String, taskDescription: String) {
        val ref = database.getReference("groups/$groupNumber/homework/$taskName")
        ref.setValue(taskDescription)
            .addOnSuccessListener {
                println("Homework added successfully")
            }
            .addOnFailureListener { error ->
                println("Failed to add homework: ${error.message}")
            }
    }

    fun removeHomework(groupNumber: String, taskName: String) {
        val ref = database.getReference("groups/$groupNumber/homework/$taskName")
        ref.removeValue()
            .addOnSuccessListener {
                println("Homework removed successfully")
            }
            .addOnFailureListener { error ->
                println("Failed to remove homework: ${error.message}")
            }
    }
}