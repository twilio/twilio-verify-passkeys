/*
 * Copyright © 2024 Twilio.
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

package com.twilio.passkeys.android.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twilio.passkeys.android.R
import com.twilio.passkeys.android.ui.button_color
import com.twilio.passkeys.android.ui.twilio

@Suppress("FunctionName")
@Composable
@Preview
fun LoginPage(
  onNumberEntered: (phoneNumber: String) -> Unit = {},
  fetchPasskeys: () -> Unit = {},
  numberError: Boolean = false,
) {
  val number = remember { mutableStateOf(TextFieldValue()) }
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(horizontal = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
      painter = painterResource(id = R.drawable.owl_image),
      contentDescription = "owl",
      contentScale = ContentScale.Fit,
      modifier =
        Modifier
          .padding(top = 100.dp)
          .clickable { fetchPasskeys() },
    )
    Text(
      text =
        buildAnnotatedString {
          append("Welcome to ")
          withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
            append("OwlBank")
          }
        },
      modifier = Modifier.padding(top = 32.dp),
      style =
        TextStyle(
          fontSize = 18.sp,
          lineHeight = 28.sp,
          fontWeight = FontWeight(400),
          color = Color(0xFF121C2D),
        ),
    )
    Text(
      text = "What's your phone number?",
      modifier = Modifier.padding(top = 8.dp),
      style =
        TextStyle(
          fontSize = 24.sp,
          lineHeight = 32.sp,
          fontWeight = FontWeight(600),
          color = Color(0xFF121C2D),
          textAlign = TextAlign.Center,
        ),
    )
    OutlinedTextField(
      label = { Text(text = "Phone number") },
      value = number.value,
      onValueChange = { number.value = it },
      isError = numberError,
      supportingText = { if (numberError) Text(text = "Invalid input, please type a valid phone number") },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
      singleLine = true,
      modifier =
        Modifier
          .testTag("phone_number")
          .fillMaxWidth()
          .padding(top = 4.dp),
      trailingIcon = {
        Icon(
          painter = painterResource(R.drawable.twilio),
          contentDescription = "Twilio",
          tint = twilio,
        )
      },
      shape = RoundedCornerShape(8.dp),
    )
    Button(
      modifier =
        Modifier
          .testTag("submit")
          .fillMaxWidth()
          .height(72.dp)
          .padding(top = 16.dp),
      onClick = { onNumberEntered(number.value.text) },
      colors =
        ButtonDefaults.buttonColors(
          containerColor = button_color,
        ),
      shape = RoundedCornerShape(8.dp),
    ) {
      Text(
        text = "Get started",
        style =
          TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(600),
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center,
          ),
      )
    }
  }
}
