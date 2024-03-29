/*
 * Copyright © 2024 Twilio Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
