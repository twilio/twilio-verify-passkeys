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

import android.app.Activity
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialDomException
import com.google.common.truth.Truth.assertThat
import com.twilio.passkeys.exception.TwilioException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AndroidTwilioPasskeyTest {
  private val credentialManager: CredentialManager = mockk()
  private val passkeyPayloadMapper: PasskeyPayloadMapper = mockk()
  private val twilioPasskey = com.twilio.passkeys.TwilioPasskey(credentialManager, passkeyPayloadMapper)
  private val activity: Activity = mockk()
  private val appContext = com.twilio.passkeys.AppContext(activity)

  @Before
  fun before() {
    every { passkeyPayloadMapper.mapToPasskeyCreationPayload(createPayload) } returns createPasskeyRequest
    every { passkeyPayloadMapper.mapToPasskeyCreationResponse(createResultPayload) } returns createPasskeyResponse

    every { passkeyPayloadMapper.mapToPasskeyAuthenticationPayload(authenticatePayload) } returns authenticatePasskeyRequest
    every { passkeyPayloadMapper.mapToAuthenticatePasskeyResponse(AUTHENTICATOR_RESULT_PAYLOAD) } returns authenticatePasskeyResponse
  }

  @Test
  fun `Create passkey with json payload succeeds`() {
    val response: CreatePublicKeyCredentialResponse = mockk()
    every { response.registrationResponseJson } returns createResultPayload
    coEvery { credentialManager.createCredential(eq(activity), any()) } returns response

    runTest {
      val result =
        twilioPasskey.create(
          challengePayload = createPayload,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Success::class.java)
      assertThat((result as CreatePasskeyResult.Success).createPasskeyResponse.id).isEqualTo(
        ID,
      )
      assertThat(result.createPasskeyResponse.rawId).isEqualTo(RAW_ID)
      assertThat(result.createPasskeyResponse.authenticatorAttachment).isEqualTo(
        AUTHENTICATOR_ATTACHMENT,
      )
      assertThat(result.createPasskeyResponse.type).isEqualTo(TYPE)
      assertThat(result.createPasskeyResponse.attestationObject).isEqualTo(
        ATTESTATION_OBJECT,
      )
      assertThat(result.createPasskeyResponse.clientDataJSON).isEqualTo(
        CLIENT_DATA_JSON_CREATE,
      )
      assertThat(result.createPasskeyResponse.transports).isEqualTo(transports)
    }
  }

  @Test
  fun `Create passkey with json payload fails`() {
    val exception = SerializationException("error")
    val expectedException = TwilioException("error")
    every { passkeyPayloadMapper.mapToPasskeyCreationPayload(createPayload) } throws exception
    every { passkeyPayloadMapper.mapException(exception) } returns expectedException
    runTest {
      val result =
        twilioPasskey.create(
          challengePayload = createPayload,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
      assertThat((result as CreatePasskeyResult.Error).error).isEqualTo(expectedException)
    }
  }

  @Test
  fun `Create passkey succeeds`() {
    val response: CreatePublicKeyCredentialResponse = mockk()
    every { response.registrationResponseJson } returns createResultPayload
    coEvery { credentialManager.createCredential(eq(activity), any()) } returns response

    runTest {
      val result =
        twilioPasskey.create(
          createPasskeyRequest = createPasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Success::class.java)
      assertThat((result as CreatePasskeyResult.Success).createPasskeyResponse.id).isEqualTo(
        ID,
      )
      assertThat(result.createPasskeyResponse.rawId).isEqualTo(RAW_ID)
      assertThat(result.createPasskeyResponse.authenticatorAttachment).isEqualTo(
        AUTHENTICATOR_ATTACHMENT,
      )
      assertThat(result.createPasskeyResponse.type).isEqualTo(TYPE)
      assertThat(result.createPasskeyResponse.attestationObject).isEqualTo(
        ATTESTATION_OBJECT,
      )
      assertThat(result.createPasskeyResponse.clientDataJSON).isEqualTo(
        CLIENT_DATA_JSON_CREATE,
      )
      assertThat(result.createPasskeyResponse.transports).isEqualTo(transports)
    }
  }

  @Test
  fun `Create passkey fails with DomError`() {
    coEvery {
      credentialManager.createCredential(
        eq(activity),
        any(),
      )
    } throws
      CreatePublicKeyCredentialDomException(
        mockk(relaxed = true),
      )

    runTest {
      val result =
        twilioPasskey.create(
          createPasskeyRequest = createPasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
      assertThat((result as CreatePasskeyResult.Error).error).isInstanceOf(TwilioException::class.java)
    }
  }

  @Test
  fun `Create passkey fails with CancellationError`() {
    coEvery {
      credentialManager.createCredential(
        eq(activity),
        any(),
      )
    } throws
      CreateCredentialCancellationException(
        mockk(relaxed = true),
      )

    runTest {
      val result =
        twilioPasskey.create(
          createPasskeyRequest = createPasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
      assertThat((result as CreatePasskeyResult.Error).error).isInstanceOf(TwilioException::class.java)
    }
  }

  @Test
  fun `Create passkey fails with InterruptedError`() {
    coEvery {
      credentialManager.createCredential(
        eq(activity),
        any(),
      )
    } throws
      CreateCredentialInterruptedException(
        mockk(relaxed = true),
      )

    runTest {
      val result =
        twilioPasskey.create(
          createPasskeyRequest = createPasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
      assertThat((result as CreatePasskeyResult.Error).error).isInstanceOf(TwilioException::class.java)
    }
  }

  @Test
  fun `Create passkey fails with ProviderConfigurationError`() {
    coEvery {
      credentialManager.createCredential(
        eq(activity),
        any(),
      )
    } throws
      CreateCredentialProviderConfigurationException(
        mockk(relaxed = true),
      )

    runTest {
      val result =
        twilioPasskey.create(
          createPasskeyRequest = createPasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
      assertThat((result as CreatePasskeyResult.Error).error).isInstanceOf(TwilioException::class.java)
    }
  }

  @Test
  fun `Create passkey fails with CustomError`() {
    coEvery {
      credentialManager.createCredential(
        eq(activity),
        any(),
      )
    } throws CreateCredentialCustomException("custom")

    runTest {
      val result =
        twilioPasskey.create(
          createPasskeyRequest = createPasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
      assertThat((result as CreatePasskeyResult.Error).error).isInstanceOf(TwilioException::class.java)
    }
  }

  @Test
  fun `Create passkey fails with UnknownError`() {
    coEvery {
      credentialManager.createCredential(
        eq(activity),
        any(),
      )
    } throws
      CreateCredentialUnknownException(
        mockk(relaxed = true),
      )

    runTest {
      val result =
        twilioPasskey.create(
          createPasskeyRequest = createPasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(CreatePasskeyResult.Error::class.java)
      assertThat((result as CreatePasskeyResult.Error).error).isInstanceOf(TwilioException::class.java)
    }
  }

  @Test
  fun `Authenticate passkey with json payload succeeds`() {
    val response: GetCredentialResponse = mockk()
    val credential: PublicKeyCredential = mockk()
    every { response.credential } returns credential
    every { credential.authenticationResponseJson } returns AUTHENTICATOR_RESULT_PAYLOAD
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } returns response

    runTest {
      val result =
        twilioPasskey.authenticate(
          challengePayload = authenticatePayload,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Success::class.java)
      assertThat((result as AuthenticatePasskeyResult.Success).authenticatePasskeyResponse.id).isEqualTo(
        ID,
      )
      assertThat(result.authenticatePasskeyResponse.rawId).isEqualTo(RAW_ID)
      assertThat(result.authenticatePasskeyResponse.authenticatorAttachment).isEqualTo(
        AUTHENTICATOR_ATTACHMENT,
      )
      assertThat(result.authenticatePasskeyResponse.type).isEqualTo(TYPE)
      assertThat(result.authenticatePasskeyResponse.clientDataJSON).isEqualTo(
        CLIENT_DATA_JSON_AUTHENTICATE,
      )
      assertThat(result.authenticatePasskeyResponse.authenticatorData).isEqualTo(
        AUTHENTICATOR_DATA,
      )
      assertThat(result.authenticatePasskeyResponse.signature).isEqualTo(SIGNATURE)
      assertThat(result.authenticatePasskeyResponse.userHandle).isEqualTo(USER_HANDLE)
    }
  }

  @Test
  fun `Authenticate passkey with json payload fails`() {
    val exception = SerializationException("error")
    val expectedException = TwilioException("error")
    every { passkeyPayloadMapper.mapToPasskeyAuthenticationPayload(authenticatePayload) } throws exception
    every { passkeyPayloadMapper.mapException(exception) } returns expectedException

    runTest {
      val result =
        twilioPasskey.authenticate(
          challengePayload = authenticatePayload,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isEqualTo(expectedException)
    }
  }

  @Test
  fun `Authenticate passkey succeeds`() {
    val response: GetCredentialResponse = mockk()
    val credential: PublicKeyCredential = mockk()
    every { response.credential } returns credential
    every { credential.authenticationResponseJson } returns AUTHENTICATOR_RESULT_PAYLOAD
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } returns response

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Success::class.java)
      assertThat((result as AuthenticatePasskeyResult.Success).authenticatePasskeyResponse.id).isEqualTo(
        ID,
      )
      assertThat(result.authenticatePasskeyResponse.rawId).isEqualTo(RAW_ID)
      assertThat(result.authenticatePasskeyResponse.authenticatorAttachment).isEqualTo(
        AUTHENTICATOR_ATTACHMENT,
      )
      assertThat(result.authenticatePasskeyResponse.type).isEqualTo(TYPE)
      assertThat(result.authenticatePasskeyResponse.clientDataJSON).isEqualTo(
        CLIENT_DATA_JSON_AUTHENTICATE,
      )
      assertThat(result.authenticatePasskeyResponse.authenticatorData).isEqualTo(
        AUTHENTICATOR_DATA,
      )
      assertThat(result.authenticatePasskeyResponse.signature).isEqualTo(SIGNATURE)
      assertThat(result.authenticatePasskeyResponse.userHandle).isEqualTo(USER_HANDLE)
    }
  }

  @Test
  fun `Authenticate passkey fails with CancellationError`() {
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } throws GetCredentialCancellationException(mockk(relaxed = true))

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isInstanceOf(
        TwilioException::class.java,
      )
    }
  }

  @Test
  fun `Authenticate passkey fails with CustomError`() {
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } throws GetCredentialCustomException("custom")

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isInstanceOf(
        TwilioException::class.java,
      )
    }
  }

  @Test
  fun `Authenticate passkey fails with InterruptedError`() {
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } throws GetCredentialInterruptedException(mockk(relaxed = true))

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isInstanceOf(
        TwilioException::class.java,
      )
    }
  }

  @Test
  fun `Authenticate passkey fails with NoCredentialError`() {
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } throws NoCredentialException(mockk(relaxed = true))

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isInstanceOf(
        TwilioException::class.java,
      )
    }
  }

  @Test
  fun `Authenticate passkey fails with ProviderConfigurationError`() {
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } throws GetCredentialProviderConfigurationException(mockk(relaxed = true))

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isInstanceOf(
        TwilioException::class.java,
      )
    }
  }

  @Test
  fun `Authenticate passkey fails with UnsupportedError`() {
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } throws GetCredentialUnsupportedException(mockk(relaxed = true))

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isInstanceOf(
        TwilioException::class.java,
      )
    }
  }

  @Test
  fun `Authenticate passkey fails with DomError`() {
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } throws GetPublicKeyCredentialDomException(mockk(relaxed = true))

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isInstanceOf(
        TwilioException::class.java,
      )
    }
  }

  @Test
  fun `Authenticate passkey fails with UnknownError`() {
    coEvery {
      credentialManager.getCredential(
        eq(activity),
        any<GetCredentialRequest>(),
      )
    } throws GetCredentialUnknownException(mockk(relaxed = true))

    runTest {
      val result =
        twilioPasskey.authenticate(
          authenticatePasskeyRequest = authenticatePasskeyRequest,
          appContext = appContext,
        )

      assertThat(result).isInstanceOf(AuthenticatePasskeyResult.Error::class.java)
      assertThat((result as AuthenticatePasskeyResult.Error).error).isInstanceOf(
        TwilioException::class.java,
      )
    }
  }
}
