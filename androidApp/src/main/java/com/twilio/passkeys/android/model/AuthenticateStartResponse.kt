package com.twilio.passkeys.android.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateStartResponse(
    val publicKey: PublicKeyCredential,
)

@Serializable
data class PublicKeyCredential(
    val challenge: String,
    val timeout: Int,
    val rpId: String,
    val allowCredentials: List<AllowCredential>,
    val userVerification: String,
)

@Serializable
data class AllowCredential(
  val id: String,
  val type: String,
  val transports: List<String>,
)
