package com.ilya.myguap.Menu.ui.UI


import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ilya.myguap.Menu.DataModel.GroupData
import com.ilya.myguap.Menu.Logic.MyViewModel
import com.ilya.myguap.Menu.Logic.ScheduleViewModel
import com.ilya.reaction.logik.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun GetGroupDataScreen(
    viewModel: MyViewModel,
    context: Context,
    currentUser: String,
    ScheduleviewModel: ScheduleViewModel,
    navController: NavController
) {

    val groupNumber = PreferenceHelper.getidgroup(context)
    var groupData by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var googletable by remember { mutableStateOf("") }
    var guaplk by remember { mutableStateOf("") }
    var navigation by remember { mutableStateOf("") }
    var homeworks by remember { mutableStateOf<List<Pair<String, String>>?>(null) }
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF191C20) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    var isExpanded by remember { mutableStateOf(false) }
    val size by animateDpAsState(targetValue = if (isExpanded) 800.dp else 50.dp)
    var lastClickTime by remember { mutableStateOf(0L) }

    LaunchedEffect(key1 = groupNumber) {
        viewModel.observeGroupData(
            groupNumber.toString(),
            onDataChange = { data ->
                groupData = data
                isLoading = false
                isError = false
                if (data != null) {
                    val admins = data["админы"] as? Map<*, *>
                    isAdmin = (admins?.get("главныйАдмин") == currentUser ||
                            (admins?.get("помощники") as? List<*>)?.contains(currentUser) == true)
                    // Извлекаем домашние задания
                    val rawHomeworks = data["homeWork"] as? Map<String, String>
                    homeworks = rawHomeworks?.map { (name, description) ->
                        name to description
                    }
                    groupData?.let { data ->
                        googletable = data["googletabel"].toString()
                        guaplk = data["guaplk"].toString()
                        navigation = data["navigation"].toString()
                    }
                }
            },
            onError = {
                isError = true
                isLoading = false
            }
        )
    }

    Column(Modifier
        .fillMaxSize()
        .padding(10.dp)
        .background(backgroundColor)
    ) {


        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Данные группы",

            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))


        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (isError) {
            Text(
                text = "Не удалось загрузить данные группы",
                color = MaterialTheme.colorScheme.error
            )
        } else {


            ExpandableWebView(googletable, "Оценки")


            Spacer(modifier = Modifier.height(10.dp))


            ExpandableWebView(navigation, "Навигация в здании")


            Spacer(modifier = Modifier.height(10.dp))


            ExpandableWebView(guaplk, "Личный кабинет ГУАП")


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp) // Добавляет отступ между элементами
            ) {

                item {
                    GroupScheduleScreen(ScheduleviewModel, groupNumber.toString())
                }


                item {
                    HomeworkList(
                        isAdmin,
                        viewModel,
                        groupNumber.toString(),
                        homeworks = homeworks,
                    )
                }
                // Добавляем Spacer (пространство между элементами)
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Добавляем GroupManagementScreen
                item {
                    GroupManagementScreen(
                        viewModel,
                        groupNumber.toString(),
                        currentUser,
                        isAdmin,
                        navController,
                        context
                    )
                }

            }


        }
    }
}


@Composable
fun HomeworkList(
    admin: Boolean,
    viewModel: MyViewModel,
    groupNumber: String,
    homeworks: List<Pair<String, String>>?,
    textColor: Color = if (isSystemInDarkTheme()) Color.White else Color.Black,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val size by animateDpAsState(targetValue = if (isExpanded) 800.dp else 80.dp)
    var lastClickTime by remember { mutableStateOf(0L) }

    // Состояния для формы создания задачи
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                        text = "Домашние задания",
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





            Spacer(modifier = Modifier.height(10.dp))

            if (isExpanded) {
                if (admin) {
                    // Форма создания новой задачи
                    CreateHomeworkForm(
                        onTaskCreated = { name, description ->
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.addHomeWork(groupNumber, name, description)
                            }
                            taskName = "" // Очищаем поле после создания
                            taskDescription = ""
                        },
                        taskName = taskName,
                        onTaskNameChange = { taskName = it },
                        taskDescription = taskDescription,
                        onTaskDescriptionChange = { taskDescription = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(homeworks ?: emptyList()) { (name, description) ->
                        HomeWork(
                            name = name,
                            description = description,
                            isAdmin = admin,
                            onEdit = { newDescription ->
                                CoroutineScope(Dispatchers.IO).launch {
                                   /// viewModel.updateHomework(groupNumber, name, newDescription)
                                }
                            },
                            onDelete = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteHomework(groupNumber, name)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun CreateHomeworkForm(
    onTaskCreated: (String, String) -> Unit,
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    taskDescription: String,
    onTaskDescriptionChange: (String) -> Unit
) {

    val background_color = if (isSystemInDarkTheme()) Color(0xFF191C20)
    else Color(0xFFFFFFFF)
    val text = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFF191C20)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        TextField(
            value = taskName,
            onValueChange = onTaskNameChange,
            label = { Text("Название задачи") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
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
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = taskDescription,
            onValueChange = onTaskDescriptionChange,
            label = { Text("Описание задачи") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
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
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (taskName.isNotBlank() && taskDescription.isNotBlank()) {
                    onTaskCreated(taskName, taskDescription)
                }
            },
            enabled = taskName.isNotBlank() && taskDescription.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Создать задачу")
        }
    }
}

@Composable
fun HomeWork(
    name: String,
    description: String,
    isAdmin: Boolean,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF191C20) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.8f),
                fontSize = 14.sp
            )

            if (isAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {


                    IconButton(onClick = {
                        onDelete()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = textColor )
                    }
                }
            }
        }
    }
}

// Функция для получения нового описания от пользователя
fun getDescriptionFromUser(currentDescription: String): String? {
    var updatedDescription: String? = null
    // Здесь можно использовать диалоговое окно или другой способ ввода текста
    // Например, показать AlertDialog с TextField
    return updatedDescription
}

@Composable
fun WebViewItem(url: String, initialScale: Int = 100) {
    var webView: WebView? = remember { null }
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                    }
                }
            },
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }
                setInitialScale(initialScale)
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                // Отключаем прокрутку WebView
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.evaluateJavascript("document.body.style.zoom = '0.7';", null)
                    }
                }
                webChromeClient = WebChromeClient()
                loadUrl(url)
                webView = this
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}


@Composable
fun ExpandableWebView(url: String, name: String) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF191C20) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    var isExpanded by remember { mutableStateOf(false) }
    val size by animateDpAsState(targetValue = if (isExpanded) 800.dp else 50.dp)

    // Создаем NestedScrollConnection для управления прокруткой
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Отключаем прокрутку родительского контейнера
                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    isExpanded = !isExpanded
                })
            }
            .nestedScroll(nestedScrollConnection) // Применяем NestedScrollConnection
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
                    text = name,
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

        if (isExpanded) {
            WebViewItem(url)
        }
    }
}


@Composable
fun GroupManagementScreen(
    viewModel: MyViewModel,
    groupNumber: String,
    userUid: String,
    isAdmin: Boolean,
    navController: NavController,
    context: Context
) {
    var googleSheetLink by remember { mutableStateOf("") }
    var communityLink by remember { mutableStateOf("") }
    var navigation by remember { mutableStateOf("") }
    var guaplk by remember { mutableStateOf("") }

    val background_color = if (isSystemInDarkTheme()) Color(0xFF191C20)
    else Color(0xFFFFFFFF)
    val text = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFF191C20)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isAdmin) {
            // Форма для редактирования полей
            TextField(
                value = googleSheetLink,
                onValueChange = { googleSheetLink = it },
                label = { Text("Google Sheet Link") },
                modifier = Modifier.fillMaxWidth(),
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
            )


            Spacer(modifier = Modifier.height(8.dp))


            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        if(googleSheetLink != "")
                        viewModel.updateField(groupNumber, "googletabel", googleSheetLink)
                        if(communityLink != "")
                        viewModel.updateField(groupNumber, "communityLink", communityLink)
                        if(navigation != "")
                        viewModel.updateField(groupNumber, "navigation", navigation)
                        if (guaplk != "")
                        viewModel.updateField(groupNumber, "guaplk", guaplk)
                    }
                },
                enabled = googleSheetLink.isNotBlank() || communityLink.isNotBlank() || navigation.isNotBlank() || guaplk.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить изменения")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для выхода из группы
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.leaveGroup(groupNumber, userUid)
                }
                PreferenceHelper.removeGroupId(context)
                navController.navigate("start")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Покинуть группу", color = Color.White)
        }
    }
}