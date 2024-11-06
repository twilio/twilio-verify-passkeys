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

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.twilio.passkeys

import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyRequest

/**
 * Class representing Passkey functionality.
 * This class provides methods to create and authenticate passkeys.
 */
expect open class TwilioPasskeys {
  /**
   * Creates a passkey using the provided [createPasskeyRequest] and [appContext].
   *
   * @param createPasskeyRequest The request to create a passkey.
   * @param appContext The context of the application.
   * @return The result of creating the passkey.
   */
  suspend fun create(
    createPasskeyRequest: CreatePasskeyRequest,
    appContext: AppContext,
  ): CreatePasskeyResult

  /**
   * Creates a passkey using the provided [createPayload] and [appContext].
   *
   * @param createPayload The payload for creating the passkey.
   * @param appContext The context of the application.
   * @return The result of creating the passkey.
   */
  suspend fun create(
    createPayload: String,
    appContext: AppContext,
  ): CreatePasskeyResult

  /**
   * Authenticates a passkey using the provided [authenticatePasskeyRequest] and [appContext].
   *
   * @param authenticatePasskeyRequest The request to authenticate a passkey.
   * @param appContext The context of the application.
   * @return The result of authenticating the passkey.
   */
  suspend fun authenticate(
    authenticatePasskeyRequest: AuthenticatePasskeyRequest,
    appContext: AppContext,
  ): AuthenticatePasskeyResult

  /**
   * Authenticates a passkey using the provided [authenticatePayload] and [appContext].
   *
   * @param authenticatePayload The payload for authenticating the passkey challenge.
   * @param appContext The context of the application.
   * @return The result of authenticating the passkey.
   */
  suspend fun authenticate(
    authenticatePayload: String,
    appContext: AppContext,
  ): AuthenticatePasskeyResult
}

/**
 * An interface representing the context.
 */
expect open class AppContext
