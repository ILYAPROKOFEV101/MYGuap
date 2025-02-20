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
import androidx.compose.material3.ButtonDefaults
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
import com.ilya.reaction.logik.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Creat_Group(
    viewModel: MyViewModel,
    modifier: Modifier = Modifier,
    context: Context,
    uid: String,
    onGroupCreated: (Boolean) -> Unit // Колбэк для уведомления о результате
) {
    val background_color = if (isSystemInDarkTheme()) Color(0xFF191C20) else Color(0xFFFFFFFF)
    val text = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFF191C20)

    var groupNumber by remember { mutableStateOf("") }
    var googleSheetLink by remember { mutableStateOf("") }
    var communityLink by remember { mutableStateOf("") }
    var navigator by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create New Group",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = groupNumber,
            onValueChange = { groupNumber = it },
            label = { Text("Group Number") },
            colors = TextFieldDefaults.colors(
                focusedTextColor = text,
                unfocusedTextColor = text,
                focusedLabelColor = text,
                unfocusedLabelColor = text,
                focusedContainerColor = background_color,
                unfocusedContainerColor = background_color,
                focusedIndicatorColor = text,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = text,
                focusedPlaceholderColor = text,
                unfocusedPlaceholderColor = text
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = googleSheetLink,
            onValueChange = { googleSheetLink = it },
            label = { Text("Google Sheet Link") },
            colors = TextFieldDefaults.colors(
                focusedTextColor = text,
                unfocusedTextColor = text,
                focusedLabelColor = text,
                unfocusedLabelColor = text,
                focusedContainerColor = background_color,
                unfocusedContainerColor = background_color,
                focusedIndicatorColor = text,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = text,
                focusedPlaceholderColor = text,
                unfocusedPlaceholderColor = text
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = communityLink,
            onValueChange = { communityLink = it },
            label = { Text("Community Link") },
            colors = TextFieldDefaults.colors(
                focusedTextColor = text,
                unfocusedTextColor = text,
                focusedLabelColor = text,
                unfocusedLabelColor = text,
                focusedContainerColor = background_color,
                unfocusedContainerColor = background_color,
                focusedIndicatorColor = text,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = text,
                focusedPlaceholderColor = text,
                unfocusedPlaceholderColor = text
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = navigator,
            onValueChange = { navigator = it },
            label = { Text("Navigator") },
            colors = TextFieldDefaults.colors(
                focusedTextColor = text,
                unfocusedTextColor = text,
                focusedLabelColor = text,
                unfocusedLabelColor = text,
                focusedContainerColor = background_color,
                unfocusedContainerColor = background_color,
                focusedIndicatorColor = text,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = text,
                focusedPlaceholderColor = text,
                unfocusedPlaceholderColor = text
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (groupNumber.isNotEmpty() && googleSheetLink.isNotEmpty() && communityLink.isNotEmpty()) {
                    // Запускаем корутину для вызова suspend-функции
                    CoroutineScope(Dispatchers.Main).launch {
                        val isCreated = viewModel.createGroup(groupNumber, uid, googleSheetLink, navigator, communityLink)
                        onGroupCreated(isCreated) // Передаем результат через колбэк
                        if(isCreated) {
                            PreferenceHelper.saveidgroup(context, groupNumber)
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Create Group")
        }
    }
}