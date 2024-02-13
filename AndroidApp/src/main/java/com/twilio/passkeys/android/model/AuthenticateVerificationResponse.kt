package com.twilio.passkeys.android.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateVerificationResponse(val status: String)
