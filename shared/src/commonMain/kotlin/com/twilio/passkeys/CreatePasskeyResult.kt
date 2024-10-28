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

package com.twilio.passkeys

import com.twilio.passkeys.exception.TwilioException2
import com.twilio.passkeys.models.CreatePasskeyResponse

/**
 * Sealed class representing the result of creating a passkey.
 */
sealed class CreatePasskeyResult {
  /**
   * Represents a successful passkey creation result.
   *
   * @param createPasskeyResponse The response containing information about the created passkey.
   */
  data class Success(val createPasskeyResponse: CreatePasskeyResponse) : CreatePasskeyResult()

  /**
   * Represents an error that occurred during passkey creation.
   *
   * @param error The exception representing the error.
   */
  data class Error(val error: TwilioException2) : CreatePasskeyResult()
}
