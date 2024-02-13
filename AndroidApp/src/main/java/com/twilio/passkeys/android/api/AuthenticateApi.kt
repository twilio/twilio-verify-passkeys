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
