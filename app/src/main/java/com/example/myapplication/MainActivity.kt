package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import  com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ViewModel.RegistrationViewModel
import com.example.myapplication.Screens.RegistrationScreen
import com.example.myapplication.Screens.DocumentsScreen
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "registration") {
                    composable("registration") {
                        val viewModel: RegistrationViewModel = hiltViewModel()

                        RegistrationScreen(
                            navController = navController,
                            viewModel = viewModel,
//                            onSubmitClick = {
//                                navController.navigate("documents_screen")
//                            }
                        )
                    }
                    composable("documents_screen") { navBackStackEntry ->
                        DocumentsScreen(
                            navBackStackEntry = navBackStackEntry,
                            navController = navController
                        )
                    }




                }
            }
        }
    }
}
