package com.ilya.myguap.Menu.ui.UI

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ilya.reaction.logik.PreferenceHelper


@Composable
fun RegistrationScreen(
    context: Context,
    onRegisterClicked: (String) -> Unit
) {


    var groupNumber by remember { mutableStateOf("") }
    val background_color = if (isSystemInDarkTheme()) Color(0xFF191C20)
    else Color(0xFFFFFFFF)
    val text = if (isSystemInDarkTheme())  Color(0xFFFFFFFF) else
        Color(0xFF191C20)

    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Имя") },
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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Фамилия") },
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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                PreferenceHelper.savefirstname(context,firstName.toString() )
                PreferenceHelper.savelastnme(context,lastName.toString() )
                val fullName = "${firstName.text} ${lastName.text}"
                onRegisterClicked(fullName)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Сохранить")
        }
    }
}

