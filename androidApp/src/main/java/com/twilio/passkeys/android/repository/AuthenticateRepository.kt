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

package com.twilio.passkeys.android.repository

import com.twilio.passkeys.android.api.AuthenticateApi
import com.twilio.passkeys.android.api.AuthenticateVerificationRequest
import com.twilio.passkeys.android.model.AuthenticateStartResponse
import com.twilio.passkeys.android.model.AuthenticateVerificationResponse
import javax.inject.Inject

class AuthenticateRepository
  @Inject
  constructor(private val authenticateApi: AuthenticateApi) {
    suspend fun start(): AuthenticateStartResponse {
      return authenticateApi.authenticateStart()
    }

    suspend fun verification(
      rawId: String,
      id: String,
      clientDataJSON: String,
      userHandle: String?,
      signature: String?,
      authenticatorData: String?,
    ): AuthenticateVerificationResponse {
      return authenticateApi.authenticateVerification(
        AuthenticateVerificationRequest(
          rawId = rawId,
          id = id,
          clientDataJSON = clientDataJSON,
          userHandle = userHandle,
          signature = signature,
          authenticatorData = authenticatorData,
        ),
      )
    }
  }
