/*
 * Copyright © 2024 Twilio Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twilio.passkeys.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.twilio.passkeys.android.pages.HomePage
import com.twilio.passkeys.android.pages.LoginPage
import com.twilio.passkeys.android.ui.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private var username = ""

  @OptIn(ExperimentalComposeUiApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MyApplicationTheme {
        Surface(
          modifier =
            Modifier
              .fillMaxSize()
              .semantics { testTagsAsResourceId = true },
          color = MaterialTheme.colorScheme.background,
        ) {
          ScreenMain()
        }
      }
    }
  }

  @Composable
  @Suppress("FunctionName")
  fun ScreenMain(loginViewModel: LoginViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val uiState = loginViewModel.state.collectAsState(initial = LoginState.Initial).value

    LaunchedEffect(uiState) {
      when (uiState) {
        LoginState.Initial -> {
          loginViewModel.authenticate(this@MainActivity)
        }

        LoginState.UsernameError -> {}
        LoginState.Logout -> {
          navController.navigate("login")
        }

        is LoginState.PasskeyError -> {
          Toast.makeText(
            this@MainActivity,
            uiState.message,
            Toast.LENGTH_SHORT,
          ).show()
        }

        is LoginState.PasskeySuccess -> {
          navController.navigate("home/${uiState.username}")
        }
      }
    }
    NavHost(navController = navController, startDestination = "login") {
      composable("login") {
        LoginPage(
          onUsernameEntered = { value ->
            username = value
            if (loginViewModel.areFieldsValid(username)) {
              loginViewModel.create(username, this@MainActivity)
            }
          },
          fetchPasskeys = {
            loginViewModel.authenticate(this@MainActivity)
          },
          usernameError = uiState is LoginState.UsernameError,
        )
      }
      composable(
        "home/{username}",
        arguments = listOf(navArgument("username") { type = NavType.StringType }),
      ) { backStackEntry ->
        val username = backStackEntry.arguments?.getString("username")!!
        HomePage(username = username, onDisconnect = {
          loginViewModel.logout()
        })
      }
    }
  }
}
