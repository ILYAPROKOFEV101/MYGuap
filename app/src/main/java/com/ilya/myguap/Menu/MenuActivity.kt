package com.ilya.myguap.Menu

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.myguap.Menu.Logic.MyViewModel
import com.ilya.myguap.Menu.Logic.ScheduleRepository
import com.ilya.myguap.Menu.Logic.ScheduleViewModel
import com.ilya.myguap.Menu.ui.UI.Creat_Group
import com.ilya.myguap.Menu.ui.UI.DuplicateHomeworkMenu
import com.ilya.myguap.Menu.ui.UI.GetGroupDataScreen
import com.ilya.myguap.Menu.ui.UI.GroupScheduleScreen
import com.ilya.myguap.Menu.ui.UI.GroupSearchScreen
import com.ilya.myguap.Menu.ui.UI.RegistrationScreen
import com.ilya.myguap.Menu.ui.UI.StartScreen
import com.ilya.myguap.Menu.ui.theme.MyGuapTheme
import com.ilya.reaction.logik.PreferenceHelper

class MenuActivity : ComponentActivity() {

    private val viewModel: MyViewModel by viewModels()
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            val img = IMG(userData = googleAuthUiClient.getSignedInUser())
            val uid = ID(userData = googleAuthUiClient.getSignedInUser())
            val nameofmygroup = PreferenceHelper.getidgroup(this)
            val name = PreferenceHelper.getfirstnamed(this)
            val lastname = PreferenceHelper.getlastnme(this)
            MyGuapTheme {
                    NavHost(
                        navController = navController,
                        startDestination = when {
                            nameofmygroup != "" -> "mygroup"
                            name != "" && lastname != "" -> "start"
                            else -> "registtion"
                        }
                    ) {
                        composable("mygroup") {
                            GetGroupDataScreen(viewModel = viewModel, context = this@MenuActivity, uid.toString())
                        }
                        composable("registtion") {
                            RegistrationScreen(this@MenuActivity, onRegisterClicked = { fullName ->
                                if(fullName != "") {
                                    if(nameofmygroup != ""){
                                        navController.navigate("mygroup")
                                    } else
                                    navController.navigate("start")
                                }
                            })
                        }
                        composable("start") {
                            Column(modifier = Modifier.fillMaxSize())
                                {
                                    StartScreen(
                                        viewModel = viewModel,
                                        context = this@MenuActivity,
                                        uid = uid.toString(),
                                        navController
                                    )
                                }
                            }
                        composable("info") {
                            DuplicateHomeworkMenu(
                                viewModel,
                                modifier = Modifier,
                                context = this@MenuActivity
                                )
                        }
                        composable("Schedule") {
                            val repository = ScheduleRepository(applicationContext)
                            val viewModel: ScheduleViewModel = viewModel(factory = ScheduleViewModelFactory(repository))
                            GroupScheduleScreen(viewModel)
                        }
                    }

                }
            }
        }

    }





// Фабрика для создания ViewModel
class ScheduleViewModelFactory(private val repository: ScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScheduleViewModel(repository) as T
    }
}