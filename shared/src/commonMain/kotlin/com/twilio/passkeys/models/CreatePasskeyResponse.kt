package com.twilio.passkeys.models

import kotlinx.serialization.Serializable

data class CreatePasskeyResponse(
  val id: String,
  val rawId: String,
  val authenticatorAttachment: String,
  val type: String,
  val attestationObject: String,
  val clientDataJSON: String,
  val transports: List<String>,
)

@Serializable
data class CreatePasskeyDto(
  val id: String,
  val rawId: String,
  val authenticatorAttachment: String,
  val type: String,
  val response: CreatePasskeyResponseDto,
)

@Serializable
data class CreatePasskeyResponseDto(
  val attestationObject: String,
  val clientDataJSON: String,
  val transports: List<String>,
)
