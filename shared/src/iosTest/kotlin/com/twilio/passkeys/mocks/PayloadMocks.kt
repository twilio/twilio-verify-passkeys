package com.twilio.passkeys.mocks

import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.AuthenticatePasskeyRequestPublicKey
import com.twilio.passkeys.models.AuthenticatePasskeyResponse
import com.twilio.passkeys.models.AuthenticatorSelection
import com.twilio.passkeys.models.CreatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyResponse
import com.twilio.passkeys.models.KeyCredential
import com.twilio.passkeys.models.PubKeyCredParams
import com.twilio.passkeys.models.Rp
import com.twilio.passkeys.models.User

val createPasskeyChallengePayload =
  """
  {
    "rp": {
      "id": "example.com",
      "name": "Example"
    },
    "user": {
      "id": "PLACEHOLDERIDWUU0ZmQzYWFmNGU0NTMyNGQwZjNlMTM0N",
      "name": "user1",
      "displayName": "User One"
    },
    "challenge": "WUYwNDhkMWE3ZWMzYTJhNjk3MDA1OWMyNzY2YmJjN2UwZg",
    "pubKeyCredParams": [
      {
        "type": "public-key",
        "alg": -7
      }
    ],
    "timeout": 600000,
    "excludeCredentials": [

    ],
    "authenticatorSelection": {
      "authenticatorAttachment": "platform",
      "requireResidentKey": false,
      "residentKey": "preferred",
      "userVerification": "preferred"
    },
    "attestation": "none"
  }
  """.trimIndent()

const val RP_ID = "example.com"
const val RP_NAME = "Example"
const val USER_ID = "PLACEHOLDERIDWUU0ZmQzYWFmNGU0NTMyNGQwZjNlMTM0N"
const val USER_NAME = "user1"
const val USER_DISPLAY_NAME = "User One"
const val CREATE_CHALLENGE = "WUYwNDhkMWE3ZWMzYTJhNjk3MDA1OWMyNzY2YmJjN2UwZg"
const val PUB_KEY_CRED_TYPE = "public-key"
const val PUB_KEY_CRED_ALG = -7
const val TIMEOUT = 600000L
val keyCredential =
  KeyCredential(
    id = "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz-1EZrNQ",
    type = "public-key",
    listOf("internal", "hybrid"),
  )
const val AUTHENTICATOR_SELECTION_AUTHENTICATOR_ATTACHMENT = "platform"
const val AUTHENTICATOR_SELECTION_REQUIRE_RESIDENT_KEY = false
const val AUTHENTICATOR_SELECTION_RESIDENT_KEY = "preferred"
const val AUTHENTICATOR_SELECTION_USER_VERIFICATION = "preferred"
const val ATTESTATION = "none"
val createPayload =
  """
  {
    "rp": {
      "id": "$RP_ID",
      "name": "$RP_NAME"
    },
    "user": {
      "id": "$USER_ID",
      "name": "$USER_NAME",
      "displayName": "$USER_DISPLAY_NAME"
    },
    "challenge": "$CREATE_CHALLENGE",
    "pubKeyCredParams": [
      {
        "type": "$PUB_KEY_CRED_TYPE",
        "alg": $PUB_KEY_CRED_ALG
      }
    ],
    "timeout": $TIMEOUT,
    "excludeCredentials": [
      {
          "id": "${keyCredential.id}",
          "type": "${keyCredential.type}",
          "transports": [
              "${keyCredential.transports[0]}",
              "${keyCredential.transports[1]}"
          ]
      }
    ],
    "authenticatorSelection": {
      "authenticatorAttachment": "$AUTHENTICATOR_SELECTION_AUTHENTICATOR_ATTACHMENT",
      "requireResidentKey": $AUTHENTICATOR_SELECTION_REQUIRE_RESIDENT_KEY,
      "residentKey": "$AUTHENTICATOR_SELECTION_RESIDENT_KEY",
      "userVerification": "$AUTHENTICATOR_SELECTION_USER_VERIFICATION"
    },
    "attestation": "$ATTESTATION"
  }
  """.trimIndent()

const val ID = "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz"
const val RAW_ID = "eb24a684977aa8650c0ad862aaccf26f839de14e93167d2fdc32f3fb5119acd4"
const val AUTHENTICATOR_ATTACHMENT = "platform"
const val ATTESTATION_OBJECT =
  "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YViko3mm9u6vuaVeN4wRgDTidR5o" +
    "L6ufLTCrE9ISVYbOGUdFAAAAAAAAAAAAAAAAAAAAAAAAAAAAIOskpoSXeqhlDArYYqrM8m"
const val CLIENT_DATA_JSON_CREATE =
  "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiV1VZd05EaGtNV0UzWldNellUSmhOamsz" +
    "TURBMU9XTXlOelkyWW1Kak4yVXdaZyIsIm9yaWdpbiI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJjcm9zc09yaWdpbiI6ZmFsc2V9"
const val TYPE = "public-key"
val transports = listOf("internal", "hybrid")
val createResultPayload =
  """
  {
      "id": "$ID",
      "rawId": "$RAW_ID",
      "authenticatorAttachment": "$AUTHENTICATOR_ATTACHMENT",
      "response": {
          "attestationObject": "$ATTESTATION_OBJECT",
          "clientDataJSON": "$CLIENT_DATA_JSON_CREATE",
          "transports": [
              "${transports[0]}",
              "${transports[1]}"
          ]
      },
      "type": "$TYPE"
  }
  """.trimIndent()

val createPasskeyResponse =
  CreatePasskeyResponse(
    id = ID,
    rawId = RAW_ID,
    authenticatorAttachment = AUTHENTICATOR_ATTACHMENT,
    type = TYPE,
    attestationObject = ATTESTATION_OBJECT,
    clientDataJSON = CLIENT_DATA_JSON_CREATE,
    transports = transports,
  )
val createPasskeyRequest =
  CreatePasskeyRequest(
    challenge = CREATE_CHALLENGE,
    rp = Rp(name = RP_NAME, id = RP_ID, icon = null),
    user =
      User(
        id = USER_ID,
        icon = null,
        name = USER_NAME,
        displayName = USER_DISPLAY_NAME,
      ),
    pubKeyCredParams =
      listOf(
        PubKeyCredParams(
          type = PUB_KEY_CRED_TYPE,
          alg = PUB_KEY_CRED_ALG,
        ),
      ),
    timeout = TIMEOUT,
    attestation = ATTESTATION,
    excludeCredentials = emptyList(),
    authenticatorSelection =
      AuthenticatorSelection(
        authenticatorAttachment = AUTHENTICATOR_SELECTION_AUTHENTICATOR_ATTACHMENT,
        requireResidentKey = AUTHENTICATOR_SELECTION_REQUIRE_RESIDENT_KEY,
        residentKey = AUTHENTICATOR_SELECTION_RESIDENT_KEY,
        userVerification = AUTHENTICATOR_SELECTION_USER_VERIFICATION,
      ),
  )

const val AUTHENTICATE_CHALLENGE = "WUMwNDk4ZWNlYzZhZWYwYWViZjRmNmJkZjBkMTZlOGUyNw"
const val USER_VERIFICATION = "preferred"
val authenticatePayload =
  """
  {
      "publicKey": {
          "challenge": "$AUTHENTICATE_CHALLENGE",
          "timeout": $TIMEOUT,
          "rpId": "$RP_ID",
          "allowCredentials": [
              {
                  "id": "${keyCredential.id}",
                  "type": "${keyCredential.type}",
                  "transports": [
                      "${keyCredential.transports[0]}",
                      "${keyCredential.transports[1]}"
                  ]
              }
          ],
          "userVerification": "$USER_VERIFICATION"
      }
  }
  """.trimIndent()

const val SIGNATURE =
  "MEYCIQDDs662ykELzpmxkQaOR6HY5GwO7nX5z7jc7q9GbWZmvwIhAMEm4VBjWKzn60eGF8VtO6uqkRtSQpJvixCEy9Pr6E4o"
const val USER_HANDLE = "PLACEHOLDERIDWUU0ZmQzYWFmNGU0NTMyNGQwZjNlMTM0N"
const val CLIENT_DATA_JSON_AUTHENTICATE =
  "eyJ0eXBlIjoid2ViYXV0aG4uZ2V0IiwiY2hhbGxlbmdlIjoiV1VNd05EazRaV05sWXpaaFpXWXdZV1ZpWmpSbU5tSmtaakJr" +
    "TVRabE9HVXlOdyIsIm9yaWdpbiI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJjcm9zc09yaWdpbiI6ZmFsc2V9"
const val AUTHENTICATOR_DATA = "o3mm9u6vuaVeN4wRgDTidR5oL6ufLTCrE9ISVYbOGUcFAAAAAQ"
val AUTHENTICATOR_RESULT_PAYLOAD =
  """
  {
      "id": "$ID",
      "rawId": "$RAW_ID",
      "authenticatorAttachment": "$AUTHENTICATOR_ATTACHMENT",
      "type": "$TYPE",
      "response": {
          "signature": "$SIGNATURE",
          "userHandle": "$USER_HANDLE",
          "clientDataJSON": "$CLIENT_DATA_JSON_AUTHENTICATE",
          "authenticatorData": "$AUTHENTICATOR_DATA"
      }
  }
  """.trimIndent()
val authenticatePasskeyResponse =
  AuthenticatePasskeyResponse(
    id = ID,
    rawId = RAW_ID,
    authenticatorAttachment = AUTHENTICATOR_ATTACHMENT,
    type = TYPE,
    clientDataJSON = CLIENT_DATA_JSON_AUTHENTICATE,
    authenticatorData = AUTHENTICATOR_DATA,
    signature = SIGNATURE,
    userHandle = USER_HANDLE,
  )
val authenticatePasskeyRequestPublicKey =
  AuthenticatePasskeyRequestPublicKey(
    challenge = AUTHENTICATE_CHALLENGE,
    timeout = TIMEOUT,
    rpId = RP_ID,
    allowCredentials =
      listOf(
        KeyCredential(
          id = keyCredential.id,
          type = keyCredential.type,
          transports = listOf(keyCredential.transports[0], keyCredential.transports[1]),
        ),
      ),
    userVerification = USER_VERIFICATION,
  )
val authenticatePasskeyRequest = AuthenticatePasskeyRequest(authenticatePasskeyRequestPublicKey)
