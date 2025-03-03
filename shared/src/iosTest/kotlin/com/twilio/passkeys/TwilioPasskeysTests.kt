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

import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.mocks.ATTESTATION_OBJECT
import com.twilio.passkeys.mocks.AUTHENTICATOR_ATTACHMENT
import com.twilio.passkeys.mocks.AUTHENTICATOR_RESULT_PAYLOAD
import com.twilio.passkeys.mocks.AuthorizationControllerWrapperMock
import com.twilio.passkeys.mocks.CLIENT_DATA_JSON_AUTHENTICATE
import com.twilio.passkeys.mocks.CLIENT_DATA_JSON_CREATE
import com.twilio.passkeys.mocks.ID
import com.twilio.passkeys.mocks.RAW_ID
import com.twilio.passkeys.mocks.SIGNATURE
import com.twilio.passkeys.mocks.TYPE
import com.twilio.passkeys.mocks.USER_HANDLE
import com.twilio.passkeys.mocks.authenticatePayload
import com.twilio.passkeys.mocks.createPasskeyChallengePayload
import com.twilio.passkeys.mocks.createResultPayload
import com.twilio.passkeys.mocks.transports
import kotlinx.coroutines.test.runTest
import platform.UIKit.UIWindow
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

@OptIn(ExperimentalNativeApi::class)
class TwilioPasskeysTests {
  // region Properties
  private val passkeyPayloadMapper = PasskeyPayloadMapper
  private val authControllerWrapper: AuthorizationControllerWrapperMock = AuthorizationControllerWrapperMock()
  private val twilioPasskey = TwilioPasskeys(passkeyPayloadMapper, authControllerWrapper)
  // endregion

  // region Tests
  @Test
  fun `Passkey creation with a valid response should succeed`() =
    runTest {
      // Given
      val createPasskeyResponse = passkeyPayloadMapper.mapToCreatePasskeyResponse(registrationResultJson = createResultPayload)
      authControllerWrapper.createPasskeyResultValue = CreatePasskeyResult.Success(createPasskeyResponse)

      // When
      val result = twilioPasskey.create(createPayload = createPasskeyChallengePayload, appContext = AppContext(UIWindow()))

      // Then
      when (result) {
        is CreatePasskeyResult.Success -> {
          val response = result.createPasskeyResponse
          assertTrue { response.id.equals(ID) }
          assertTrue { response.rawId.equals(RAW_ID) }
          assertTrue { response.authenticatorAttachment.equals(AUTHENTICATOR_ATTACHMENT) }
          assertTrue { response.type.equals(TYPE) }
          assertTrue { response.attestationObject.equals(ATTESTATION_OBJECT) }
          assertTrue { response.clientDataJSON.equals(CLIENT_DATA_JSON_CREATE) }
          assertTrue { response.transports.equals(transports) }
        }
        is CreatePasskeyResult.Error -> {
          val error = result.error
          fail("An error was found while creating the passkey: $error")
        }
      }
    }

  @Test
  fun `Passkey creation with an invalid response should fail`() =
    runTest {
      // Given
      val expectedError = TwilioException.InvalidPayloadException(Exception("Invalid Payload Exception"))
      authControllerWrapper.createPasskeyResultValue = CreatePasskeyResult.Error(expectedError)

      // When
      val result = twilioPasskey.create(createPayload = createPasskeyChallengePayload, appContext = AppContext(UIWindow()))

      // Then
      when (result) {
        is CreatePasskeyResult.Success -> {
          fail("It shouldn't succeed")
        }
        is CreatePasskeyResult.Error -> {
          val error = result.error
          assertTrue { error.message.equals(expectedError.message) }
        }
      }
    }

  @Test
  fun `Passkey authentication with a valid response should succeed`() =
    runTest {
      // Given
      val authenticatePasskeyResponse =
        passkeyPayloadMapper.mapToAuthenticatePasskeyResponse(authenticatePasskeyResultJson = AUTHENTICATOR_RESULT_PAYLOAD)
      authControllerWrapper.authenticatePasskeyResultValue =
        AuthenticatePasskeyResult.Success(authenticatePasskeyResponse)

      // When
      val result = twilioPasskey.authenticate(authenticatePayload = authenticatePayload, appContext = AppContext(UIWindow()))

      // Then
      when (result) {
        is AuthenticatePasskeyResult.Success -> {
          val response = result.authenticatePasskeyResponse
          print(response)
          assertTrue { response.id.equals(ID) }
          assertTrue { response.rawId.equals(RAW_ID) }
          assertTrue { response.authenticatorAttachment.equals(AUTHENTICATOR_ATTACHMENT) }
          assertTrue { response.type.equals(TYPE) }
          assertTrue { response.clientDataJSON.equals(CLIENT_DATA_JSON_AUTHENTICATE) }
          assertTrue { response.signature.equals(SIGNATURE) }
          assertTrue { response.userHandle.equals(USER_HANDLE) }
        }
        is AuthenticatePasskeyResult.Error -> {
          val error = result.error
          fail("An error was found while creating the passkey: $error")
        }
      }
    }

  @Test
  fun `Passkey authentication with an invalid response should fail`() =
    runTest {
      // Given
      val expectedError = TwilioException.InvalidPayloadException(Exception("Invalid Payload Exception"))
      authControllerWrapper.authenticatePasskeyResultValue = AuthenticatePasskeyResult.Error(expectedError)

      // When
      val result = twilioPasskey.authenticate(authenticatePayload = authenticatePayload, appContext = AppContext(UIWindow()))

      // Then
      when (result) {
        is AuthenticatePasskeyResult.Success -> {
          fail("It shouldn't succeed")
        }
        is AuthenticatePasskeyResult.Error -> {
          val error = result.error
          assertTrue { error.message.equals(expectedError.message) }
        }
      }
    }

  // endregion
}
