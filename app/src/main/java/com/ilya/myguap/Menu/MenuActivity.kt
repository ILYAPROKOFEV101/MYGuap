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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.myguap.Menu.Logic.MyViewModel
import com.ilya.myguap.Menu.ui.UI.Creat_Group
import com.ilya.myguap.Menu.ui.UI.DuplicateHomeworkMenu
import com.ilya.myguap.Menu.ui.UI.GroupSearchScreen
import com.ilya.myguap.Menu.ui.theme.MyGuapTheme

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

            val name = UID(userData = googleAuthUiClient.getSignedInUser())
            val img = IMG(userData = googleAuthUiClient.getSignedInUser())
            val uid = ID(userData = googleAuthUiClient.getSignedInUser())

            MyGuapTheme {
                    NavHost(
                        navController = navController,
                        startDestination = "info"
                    ) {
                        composable("start") {
                        Column(modifier = Modifier.fillMaxSize())
                            {
                            GroupSearchScreen(
                                viewModel,
                                navController,
                                modifier = Modifier.height(100.dp),
                                context = this@MenuActivity
                            )
                             Spacer(modifier = Modifier.height(40.dp))
                             Creat_Group(
                                 viewModel,
                                 modifier = Modifier,
                                 context = this@MenuActivity,
                                 uid.toString()
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
                    }

                }
            }
        }

    }





