package com.twilio.passkeys.android.api

import com.twilio.passkeys.android.model.RegistrationStartResponse
import com.twilio.passkeys.android.model.RegistrationVerificationResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationApi {
  @POST("/registration/start")
  suspend fun registrationStart(
      @Body registrationStartRequest: RegistrationStartRequest,
  ): RegistrationStartResponse

  @POST("/registration/verification")
  suspend fun registrationVerification(
      @Body registrationVerificationRequest: RegistrationVerificationRequest,
  ): RegistrationVerificationResponse
}

@Serializable
data class RegistrationStartRequest(val username: String)

@Serializable
data class RegistrationVerificationRequest(
  val rawId: String,
  val id: String,
  val clientDataJson: String,
  val attestationObject: String,
  val type: String,
  val transports: List<String>,
)
