package com.twilio.passkeys.android.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationStartResponse(
    val rp: RP,
    val user: User,
    val challenge: String,
    val pubKeyCredParams: List<PublicKeyCredentialParam>,
    val timeout: Int,
    val excludeCredentials: List<ExcludeCredential>,
    val authenticatorSelection: AuthenticatorSelection,
    val attestation: String,
)

@Serializable
data class RP(
  val id: String,
  val name: String,
)

@Serializable
data class User(
  val id: String,
  val name: String,
  val displayName: String?,
)

@Serializable
data class PublicKeyCredentialParam(
  val type: String,
  val alg: Int,
)

@Serializable
data class ExcludeCredential(
  val id: String,
  val type: String,
  val transports: List<String>,
)

@Serializable
data class AuthenticatorSelection(
  val authenticatorAttachment: String,
  val requireResidentKey: Boolean,
  val residentKey: String,
  val userVerification: String,
)
