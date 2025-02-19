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
import androidx.compose.material3.Button
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
import com.ilya.myguap.Menu.Logic.MyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSearchScreen(
    viewModel: MyViewModel,
    navController: NavController,
    modifier: Modifier,
    context: Context
) {
    var groupNumber by remember { mutableStateOf("") }
    val background_color = if (isSystemInDarkTheme()) Color(0xFF191C20)
    else Color(0xFFFFFFFF)
    val text = if (isSystemInDarkTheme())  Color(0xFFFFFFFF) else
        Color(0xFF191C20)

    // Определение темы
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Search Group",
            style = MaterialTheme.typography.headlineSmall,
            color = if (isDarkTheme) Color.White else Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = groupNumber,
            onValueChange = { groupNumber = it },
            label = { Text("Enter Group Number", color = if (isDarkTheme) Color.White else Color.Black) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = text, // Цвет текста при фокусе
                unfocusedTextColor = text, // Цвет текста без фокуса
                focusedLabelColor = text, // Цвет метки при фокусе
                unfocusedLabelColor = text, // Цвет метки без фокуса
                focusedContainerColor = background_color, // Цвет фона при фокусе
                unfocusedContainerColor = background_color, // Цвет фона без фокуса
                focusedIndicatorColor = text, // Цвет рамки при фокусе
                unfocusedIndicatorColor = Color.Transparent, // Прозрачная рамка без фокуса
                cursorColor = text, // Цвет курсора
                focusedPlaceholderColor = text, // Цвет плейсхолдера при фокусе
                unfocusedPlaceholderColor = text // Цвет плейсхолдера без фокуса
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (groupNumber.isNotEmpty()) {

            } else {
                Toast.makeText(
                    context,
                    "Please enter a group number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text("Search", color = if (isDarkTheme) Color.Black else Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))


    }
}