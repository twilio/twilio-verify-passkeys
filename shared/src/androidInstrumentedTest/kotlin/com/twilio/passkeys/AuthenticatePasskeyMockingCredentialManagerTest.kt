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

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialDomException
import androidx.test.core.app.launchActivity
import com.google.common.truth.Truth.assertThat
import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.mocks.CredentialManagerMock
import com.twilio.passkeys.mocks.authenticateCredentialException
import com.twilio.passkeys.mocks.authenticatePasskeyChallengePayload
import com.twilio.passkeys.mocks.authenticationResponse
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthenticatePasskeyMockingCredentialManagerTest {
  private val credentialManager = CredentialManagerMock()
  private val twilioPasskeys =
    TwilioPasskeys(credentialManager, PasskeyPayloadMapper)

  @Test
  fun authenticatePasskey_succeeds() {
    authenticateCredentialException = null
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val authenticatePasskeyResult =
            twilioPasskeys.authenticate(
              authenticatePasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(authenticatePasskeyResult).isInstanceOf(AuthenticatePasskeyResult.Success::class.java)
          assertThat((authenticatePasskeyResult as AuthenticatePasskeyResult.Success).authenticatePasskeyResponse.id)
            .isEqualTo(
              authenticationResponse.id,
            )
          assertThat(authenticatePasskeyResult.authenticatePasskeyResponse.rawId).isEqualTo(
            authenticationResponse.rawId,
          )
          assertThat(authenticatePasskeyResult.authenticatePasskeyResponse.authenticatorAttachment)
            .isEqualTo(
              authenticationResponse.authenticatorAttachment,
            )
          assertThat(authenticatePasskeyResult.authenticatePasskeyResponse.type).isEqualTo(
            authenticationResponse.type,
          )
          assertThat(authenticatePasskeyResult.authenticatePasskeyResponse.clientDataJSON).isEqualTo(
            authenticationResponse.response.clientDataJSON,
          )
          assertThat(authenticatePasskeyResult.authenticatePasskeyResponse.authenticatorData).isEqualTo(
            authenticationResponse.response.authenticatorData,
          )
          assertThat(authenticatePasskeyResult.authenticatePasskeyResponse.signature).isEqualTo(
            authenticationResponse.response.signature,
          )
          assertThat(authenticatePasskeyResult.authenticatePasskeyResponse.userHandle).isEqualTo(
            authenticationResponse.response.userHandle,
          )
        }
      }
    }
  }

  @Test
  fun passkeyAuthentication_throws_getCredentialCancellationException() {
    authenticateCredentialException = mockk<GetCredentialCancellationException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.authenticate(
              authenticatePasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as AuthenticatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyAuthentication_throws_getCredentialCustomException() {
    authenticateCredentialException = mockk<GetCredentialCustomException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.authenticate(
              authenticatePasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as AuthenticatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyAuthentication_throws_getCredentialInterruptedException() {
    authenticateCredentialException = mockk<GetCredentialInterruptedException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.authenticate(
              authenticatePasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as AuthenticatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyAuthentication_throws_noCredentialException() {
    authenticateCredentialException = mockk<NoCredentialException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.authenticate(
              authenticatePasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as AuthenticatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyAuthentication_throws_getCredentialProviderConfigurationException() {
    authenticateCredentialException = mockk<GetCredentialProviderConfigurationException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.authenticate(
              authenticatePasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as AuthenticatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyAuthentication_throws_getCredentialUnsupportedException() {
    authenticateCredentialException = mockk<GetCredentialUnsupportedException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.authenticate(
              authenticatePasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as AuthenticatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyAuthentication_throws_getPublicKeyCredentialDomException() {
    authenticateCredentialException = mockk<GetPublicKeyCredentialDomException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.authenticate(
              authenticatePasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as AuthenticatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }
}
