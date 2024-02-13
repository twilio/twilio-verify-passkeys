package com.twilio.passkeys.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatePasskeyRequest(val publicKey: AuthenticatePasskeyRequestPublicKey)

@Serializable
data class AuthenticatePasskeyRequestPublicKey(
    var challenge: String,
    val timeout: Long,
    val rpId: String,
    val allowCredentials: List<KeyCredential>,
    val userVerification: String,
)
