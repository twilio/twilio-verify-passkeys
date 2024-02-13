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
      clientDataJson: String,
      attestationObject: String,
      type: String,
      transports: List<String>,
    ): RegistrationVerificationResponse {
      return registrationApi.registrationVerification(
        RegistrationVerificationRequest(
          rawId = rawId,
          id = id,
          clientDataJson = clientDataJson,
          attestationObject = attestationObject,
          type = type,
          transports = transports,
        ),
      )
    }
  }
