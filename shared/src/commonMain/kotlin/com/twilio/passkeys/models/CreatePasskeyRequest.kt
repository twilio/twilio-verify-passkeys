package com.twilio.passkeys.models

import kotlinx.serialization.Serializable

@Serializable
data class CreatePasskeyRequest(
  var challenge: String,
  val rp: Rp,
  val user: User,
  val pubKeyCredParams: List<PubKeyCredParams>,
  val timeout: Long,
  val attestation: String? = null,
  val excludeCredentials: List<KeyCredential>? = null,
  val authenticatorSelection: AuthenticatorSelection,
) {
  @Serializable
  data class Rp(
    val name: String,
    val id: String,
    val icon: String?,
  )

  @Serializable
  data class User(
    var id: String,
    var icon: String?,
    val name: String,
    val displayName: String?,
  )

  @Serializable
  data class PubKeyCredParams(
    val type: String,
    val alg: Int,
  )

  @Serializable
  data class AuthenticatorSelection(
    val authenticatorAttachment: String?,
    val requireResidentKey: Boolean?,
    val residentKey: String? = null,
    val userVerification: String?,
  )
}
