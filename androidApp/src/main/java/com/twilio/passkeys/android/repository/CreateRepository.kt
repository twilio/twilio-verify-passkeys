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

import com.twilio.passkeys.android.api.RegistrationApi
import com.twilio.passkeys.android.api.RegistrationStartRequest
import com.twilio.passkeys.android.api.RegistrationVerificationRequest
import com.twilio.passkeys.android.model.RegistrationStartResponse
import com.twilio.passkeys.android.model.RegistrationVerificationResponse
import javax.inject.Inject

class CreateRepository
  @Inject
  constructor(private val registrationApi: RegistrationApi) {
    suspend fun start(username: String): RegistrationStartResponse {
      return registrationApi.registrationStart(RegistrationStartRequest(username))
    }

    suspend fun verification(
      rawId: String,
      id: String,
      clientDataJSON: String,
      attestationObject: String,
      type: String,
      transports: List<String>,
    ): RegistrationVerificationResponse {
      return registrationApi.registrationVerification(
        RegistrationVerificationRequest(
          rawId = rawId,
          id = id,
          clientDataJSON = clientDataJSON,
          attestationObject = attestationObject,
          type = type,
          transports = transports,
        ),
      )
    }
  }
