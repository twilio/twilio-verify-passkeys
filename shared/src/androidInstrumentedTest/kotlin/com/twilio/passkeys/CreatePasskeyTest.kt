package com.twilio.passkeys

import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.mocks.createPasskeyChallengePayload
import com.twilio.passkeys.AppContext
import com.twilio.passkeys.CreatePasskeyResult
import com.twilio.passkeys.TestActivity
import com.twilio.passkeys.TwilioPasskey
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CreatePasskeyTest {
  private val twilioPasskey =
      com.twilio.passkeys.TwilioPasskey(InstrumentationRegistry.getInstrumentation().context)

  @Test
  fun createCredential_withInvalidInput_fails() {
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runBlocking {
          val result =
            twilioPasskey.create(
              challengePayload = "{invalid}",
              appContext = com.twilio.passkeys.AppContext(activity),
            )
          assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
        }
      }
    }
  }

  @Test
  fun createCredential_withNoCreateOptions_fails() {
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runBlocking {
          val result =
            twilioPasskey.create(
              challengePayload = createPasskeyChallengePayload,
              appContext = com.twilio.passkeys.AppContext(activity),
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
