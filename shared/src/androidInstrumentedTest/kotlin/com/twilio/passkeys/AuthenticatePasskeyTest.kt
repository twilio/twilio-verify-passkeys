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

package com.twilio.passkeys

import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.twilio.passkeys.AppContext
import com.twilio.passkeys.AuthenticatePasskeyResult
import com.twilio.passkeys.TestActivity
import com.twilio.passkeys.TwilioPasskey
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AuthenticatePasskeyTest {
  private val twilioPasskey =
      com.twilio.passkeys.TwilioPasskey(InstrumentationRegistry.getInstrumentation().context)

  @Test
  fun authenticateCredential_withInvalidInput_fails() {
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runBlocking {
          val result =
            twilioPasskey.authenticate(
              challengePayload = "{invalid}",
              appContext = com.twilio.passkeys.AppContext(activity),
            )
          assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
        }
      }
    }
  }
}
