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

import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.test.core.app.launchActivity
import com.google.common.truth.Truth.assertThat
import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.mocks.CredentialManagerMock
import com.twilio.passkeys.mocks.createCredentialException
import com.twilio.passkeys.mocks.createPasskeyChallengePayload
import com.twilio.passkeys.mocks.creationResponse
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CreatePasskeyMockingCredentialManagerTest {
  private val credentialManager = CredentialManagerMock()
  private val twilioPasskeys =
    TwilioPasskeys(credentialManager, PasskeyPayloadMapper)

  @Test
  fun passkeyCreation_succeeds() {
    createCredentialException = null
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.create(
              createPasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(CreatePasskeyResult.Success::class.java)
          assertThat((createPasskeyResult as CreatePasskeyResult.Success).createPasskeyResponse.id)
            .isEqualTo(
              creationResponse.id,
            )
          assertThat(createPasskeyResult.createPasskeyResponse.rawId).isEqualTo(
            creationResponse.rawId,
          )
          assertThat(createPasskeyResult.createPasskeyResponse.authenticatorAttachment)
            .isEqualTo(
              creationResponse.authenticatorAttachment,
            )
          assertThat(createPasskeyResult.createPasskeyResponse.type).isEqualTo(
            creationResponse.type,
          )
          assertThat(createPasskeyResult.createPasskeyResponse.clientDataJSON).isEqualTo(
            creationResponse.response.clientDataJSON,
          )
          assertThat(createPasskeyResult.createPasskeyResponse.attestationObject).isEqualTo(
            creationResponse.response.attestationObject,
          )
          assertThat(createPasskeyResult.createPasskeyResponse.transports).isEqualTo(
            creationResponse.response.transports,
          )
        }
      }
    }
  }

  @Test
  fun passkeyCreation_throws_createCredentialDomError() {
    createCredentialException = mockk<CreatePublicKeyCredentialDomException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.create(
              createPasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(CreatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as CreatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyCreation_throws_createCredentialCancellationError() {
    createCredentialException = mockk<CreateCredentialCancellationException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.create(
              createPasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(CreatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as CreatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyCreation_throws_createCredentialInterruptedError() {
    createCredentialException = mockk<CreateCredentialInterruptedException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.create(
              createPasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(CreatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as CreatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyCreation_throws_createCredentialProviderConfigurationError() {
    createCredentialException =
      mockk<CreateCredentialProviderConfigurationException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.create(
              createPasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(CreatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as CreatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyCreation_throws_createCredentialCustomError() {
    createCredentialException = mockk<CreateCredentialCustomException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.create(
              createPasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(CreatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as CreatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }

  @Test
  fun passkeyCreation_throws_createCredentialUnknownError() {
    createCredentialException = mockk<CreateCredentialUnknownException>(relaxed = true)
    launchActivity<TestActivity>().use { scenario ->
      scenario.onActivity { activity ->
        runTest {
          val createPasskeyResult =
            twilioPasskeys.create(
              createPasskeyChallengePayload,
              AppContext(activity),
            )
          assertThat(createPasskeyResult).isInstanceOf(CreatePasskeyResult.Error::class.java)
          assertThat((createPasskeyResult as CreatePasskeyResult.Error).error).isInstanceOf(
            TwilioException::class.java,
          )
        }
      }
    }
  }
}
