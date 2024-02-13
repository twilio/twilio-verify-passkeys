package com.twilio.passkeys.models

import kotlinx.serialization.Serializable

@Serializable
data class KeyCredential(
  val id: String,
  val type: String,
  val transports: List<String>,
)
