/*
 * Copyright © 2024 Twilio.
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

package com.twilio.passkeys.exception

import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.exception.UNKNOWN_ERROR
import kotlin.test.Test
import kotlin.test.assertEquals

class TwilioExceptionTest {
  @Test
  fun `Twilio exception with message`() {
    val message = "This is the exception message"
    val twilioException = TwilioException(message = message)

    assertEquals(twilioException.message, message)
  }

  @Test
  fun `Twilio exception with type and message`() {
    val type = UNKNOWN_ERROR
    val message = "This is the exception message"
    val twilioException = TwilioException(type = type, message = message)

    assertEquals(twilioException.message, "$type: $message")
  }
}
