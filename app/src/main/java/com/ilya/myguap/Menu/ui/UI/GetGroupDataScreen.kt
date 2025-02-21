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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ilya.myguap.Menu.DataModel.GroupData
import com.ilya.myguap.Menu.Logic.MyViewModel
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
    currentUser: String
) {
    val groupNumber = PreferenceHelper.getidgroup(context)
    var groupData by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var googletable by remember { mutableStateOf("") }
    var navigation by remember { mutableStateOf("") }
    var homeworks by remember { mutableStateOf<List<Pair<String, String>>?>(null) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(
            text = "Group Data",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (isError) {
            Text(
                text = "Failed to load group data",
                color = MaterialTheme.colorScheme.error
            )
        } else {


        ExpandableWebView(googletable, "googletable")
        Spacer(modifier = Modifier.height(40.dp))
        ExpandableWebView(navigation, "navigation")




        }
    }
}

@Composable
fun WebViewItem(url: String, initialScale: Int = 100) {
    var webView: WebView? = remember { null }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true // Включение JavaScript
                    domStorageEnabled = true // Включение DOM Storage для современных сайтов
                    setSupportZoom(true) // Включение зума
                    builtInZoomControls = true // Включение встроенных контроллеров зума
                    displayZoomControls = false // Скрытие кнопок управления зумом
                    loadWithOverviewMode = true // Адаптация контента под размер экрана
                    useWideViewPort = true // Разрешение на использование широкого viewport
                    mediaPlaybackRequiresUserGesture = false // Разрешение автовоспроизведения медиа
                    allowFileAccess = true // Разрешение доступа к локальным файлам
                    cacheMode = WebSettings.LOAD_DEFAULT // Использование кэширования
                    setGeolocationEnabled(true) // Включение геолокации
                }
                setInitialScale(initialScale) // Установка начального масштаба
                setLayerType(View.LAYER_TYPE_HARDWARE, null) // Включение аппаратного ускорения
                webViewClient = WebViewClient() // Настройка клиента WebView
                webChromeClient = WebChromeClient() // Поддержка Chrome-функций
                loadUrl(url)
                webView = this
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )

    // Пример: Изменение масштаба через JavaScript
    LaunchedEffect(Unit) {
        delay(1000) // Через 5 секунд
        webView?.evaluateJavascript("document.body.style.zoom = '0.7';", null) // Увеличение масштаба до 150%
    }
}



@Composable
fun ExpandableWebView(url: String, name: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val size by animateDpAsState(targetValue = if (isExpanded) 800.dp else 100.dp)

    // Состояние для отслеживания времени последнего клика
    var lastClickTime by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime < 300) { // Проверяем интервал между кликами
                        isExpanded = !isExpanded // Переключаем состояние при двойном клике
                    }
                    lastClickTime = currentTime // Обновляем время последнего клика
                })
            }
        
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (isExpanded) {
            WebViewItem(url)
        }
    }
}

// Панель администратора для добавления домашних заданий
@Composable
fun AdminPanelForHomeworks(
    viewModel: MyViewModel,
    groupNumber: String,
    onAddHomework: (String, String) -> Unit
) {
    var newHomeworkName by remember { mutableStateOf("") }
    var newHomeworkDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Admin Panel for Homeworks",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Поле для ввода названия домашнего задания
        OutlinedTextField(
            value = newHomeworkName,
            onValueChange = { newHomeworkName = it },
            label = { Text("Homework Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Поле для ввода описания домашнего задания
        OutlinedTextField(
            value = newHomeworkDescription,
            onValueChange = { newHomeworkDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Кнопка для добавления домашнего задания
        Button(
            onClick = {
                if (newHomeworkName.isNotEmpty() && newHomeworkDescription.isNotEmpty()) {
                    onAddHomework(newHomeworkName, newHomeworkDescription)
                    newHomeworkName = ""
                    newHomeworkDescription = ""
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = newHomeworkName.isNotEmpty() && newHomeworkDescription.isNotEmpty()
        ) {
            Text("Add Homework")
        }
    }
}