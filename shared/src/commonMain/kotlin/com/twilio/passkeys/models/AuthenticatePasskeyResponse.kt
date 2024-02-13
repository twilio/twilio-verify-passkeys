package com.twilio.passkeys.models

import kotlinx.serialization.Serializable

data class AuthenticatePasskeyResponse(
  val id: String,
  val rawId: String,
  val authenticatorAttachment: String,
  val type: String,
  val clientDataJSON: String,
  val authenticatorData: String?,
  val signature: String?,
  val userHandle: String?,
)

@Serializable
data class AuthenticatePasskeyDto(
    val rawId: String,
    val id: String,
    val authenticatorAttachment: String,
    val type: String,
    val response: AuthenticatePasskeyResponseDto,
)

@Serializable
data class AuthenticatePasskeyResponseDto(
  val clientDataJSON: String,
  val authenticatorData: String,
  val signature: String,
  val userHandle: String,
)
