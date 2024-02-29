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
  private var name = ""

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

        LoginState.NumberError -> {}
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
          navController.navigate("home/${uiState.number}")
        }
      }
    }
    NavHost(navController = navController, startDestination = "login") {
      composable("login") {
        LoginPage(
          onNumberEntered = { phoneNumber ->
            name = phoneNumber
            if (loginViewModel.areFieldsValid(phoneNumber)) {
              loginViewModel.create(name, this@MainActivity)
            }
          },
          fetchPasskeys = {
            loginViewModel.authenticate(this@MainActivity)
          },
          numberError = uiState is LoginState.NumberError,
        )
      }
      composable(
        "home/{number}",
        arguments = listOf(navArgument("number") { type = NavType.StringType }),
      ) { backStackEntry ->
        val number = backStackEntry.arguments?.getString("number")!!
        HomePage(number = number, onDisconnect = {
          loginViewModel.logout()
        })
      }
    }
  }
}
