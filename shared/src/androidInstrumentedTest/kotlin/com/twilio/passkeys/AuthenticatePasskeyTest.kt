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
