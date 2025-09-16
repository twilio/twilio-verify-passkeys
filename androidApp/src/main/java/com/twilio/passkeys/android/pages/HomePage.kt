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

package com.twilio.passkeys.android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.twilio.passkeys.android.R
import com.twilio.passkeys.android.ui.blue

@Suppress("FunctionName")
@Composable
fun HomeHeader(onLogout: () -> Unit) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Icon(
      painter = painterResource(id = R.drawable.owl_inverted),
      tint = Color.White,
      contentDescription = "Owl",
      modifier = Modifier.padding(32.dp),
    )
    IconButton(
      onClick = onLogout,
      modifier =
        Modifier
          .padding(32.dp)
          .testTag("logout"),
    ) {
      Icon(
        painter = painterResource(id = R.drawable.menu),
        tint = Color.White,
        contentDescription = "Logout",
      )
    }
  }
}

@Suppress("FunctionName")
@Composable
fun WelcomeSection(username: String) {
  Text(
    text = "Hello $username",
    style =
      TextStyle(
        fontSize = 32.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight(500),
        color = Color(0xFFFFFFFF),
      ),
    modifier = Modifier.padding(horizontal = 32.dp),
  )
  Text(
    text = "Welcome to OwlBank",
    style =
      TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(400),
        color = Color(0xB2FFFFFF),
      ),
    modifier = Modifier.padding(horizontal = 32.dp),
  )
}

@Suppress("FunctionName")
@Composable
fun SearchBar() {
  OutlinedTextField(
    leadingIcon = {
      Icon(
        imageVector = Icons.Filled.Search,
        contentDescription = "Search",
        tint = Color.White,
      )
    },
    value = "",
    onValueChange = {},
    colors =
      OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF0385EB),
        unfocusedBorderColor = Color(0xFF0385EB),
      ),
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp, vertical = 24.dp),
    shape = RoundedCornerShape(100.dp),
  )
}

@Suppress("FunctionName")
@Composable
fun ActionCard(
  icon: Int,
  text: String,
  contentDescription: String,
  useShadow: Boolean = false,
) {
  Card(
    elevation =
      CardDefaults.cardElevation(
        defaultElevation = 8.dp,
      ),
    modifier =
      Modifier
        .width(336.dp)
        .height(80.dp),
    shape = RoundedCornerShape(size = 2.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
        if (useShadow) {
          Modifier
            .shadow(
              elevation = 28.dp,
              spotColor = Color(0x08000000),
              ambientColor = Color(0x08000000),
            )
            .padding(top = 8.dp)
            .width(336.dp)
            .height(80.dp)
            .background(
              color = Color(0xFFFFFFFF),
              shape = RoundedCornerShape(size = 2.dp),
            )
        } else {
          Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
        },
    ) {
      Icon(
        modifier = Modifier.padding(start = 24.dp),
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
      )
      Text(
        modifier = Modifier.padding(start = 24.dp),
        text = text,
        style =
          TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF05090A),
            letterSpacing = 0.4.sp,
          ),
      )
    }
  }
}

@Suppress("FunctionName")
@Composable
fun ActionCardsSection() {
  ActionCard(
    icon = R.drawable.copy,
    text = "Open an account",
    contentDescription = "Open account",
    useShadow = false,
  )
  ActionCard(
    icon = R.drawable.credit_card,
    text = "Get a credit card",
    contentDescription = "Get credit card",
    useShadow = true,
  )
  ActionCard(
    icon = R.drawable.dollar,
    text = "Apply for a loan",
    contentDescription = "Apply for loan",
    useShadow = true,
  )
}

@Suppress("FunctionName")
@Composable
@Preview
fun HomePage(
  username: String = "user123",
  onLogout: () -> Unit = {},
) {
  ConstraintLayout(modifier = Modifier.fillMaxSize()) {
    val (column1, column2, spacer) = createRefs()
    Column(
      modifier =
        Modifier
          .fillMaxWidth()
          .constrainAs(column1) {
            height = Dimension.value(340.dp)
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          }
          .background(blue),
    ) {
      HomeHeader(onLogout)
      WelcomeSection(username)
      SearchBar()
    }

    Spacer(
      modifier =
        Modifier
          .height(80.dp)
          .constrainAs(spacer) {
            linkTo(top = column1.bottom, bottom = column1.bottom)
          },
    )
    Column(
      modifier =
        Modifier
          .padding(horizontal = 32.dp)
          .constrainAs(column2) {
            top.linkTo(spacer.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          },
    ) {
      ActionCardsSection()
    }
  }
}
