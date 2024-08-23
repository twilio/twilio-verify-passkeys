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

package com.twilio.passkeys

import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.mocks.createPasskeyChallengePayload
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test

class CreatePasskeyTest {
  private val twilioPasskeys =
    TwilioPasskeys(InstrumentationRegistry.getInstrumentation().context)

  @Test
  fun createCredential_withInvalidInput_fails() {
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runBlocking {
          val result =
            twilioPasskeys.create(
              createPayload = "{invalid}",
              appContext = AppContext(activity),
            )
          assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
        }
      }
    }
  }

  @Test
  @Ignore("Not working with FTL devices")
  fun createCredential_withNoCreateOptions_fails() {
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runBlocking {
          val result =
            twilioPasskeys.create(
              createPayload = createPasskeyChallengePayload,
              appContext = AppContext(activity),
            )
          assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
          assertThat((result as CreatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
          assertThat(
            result.error.message,
          ).isEqualTo("android.credentials.CreateCredentialException.TYPE_NO_CREATE_OPTIONS: No create options available.")
        }
      }
    }
  }
}
