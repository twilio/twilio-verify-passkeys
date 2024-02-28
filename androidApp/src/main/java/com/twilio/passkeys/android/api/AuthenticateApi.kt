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

package com.twilio.passkeys.android.api

import com.twilio.passkeys.android.model.AuthenticateStartResponse
import com.twilio.passkeys.android.model.AuthenticateVerificationResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticateApi {
  @POST("/authentication/start")
  suspend fun authenticateStart(): AuthenticateStartResponse

  @POST("/authentication/verification")
  suspend fun authenticateVerification(
    @Body authenticateVerificationRequest: AuthenticateVerificationRequest,
  ): AuthenticateVerificationResponse
}

@Serializable
data class AuthenticateVerificationRequest(
  val rawId: String,
  val id: String,
  val clientDataJson: String,
  val userHandle: String?,
  val signature: String?,
  val authenticatorData: String?,
)
