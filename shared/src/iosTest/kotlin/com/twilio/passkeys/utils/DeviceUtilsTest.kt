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

package com.twilio.passkeys.utils

import com.twilio.passkeys.mocks.UIDeviceMock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceUtilsTest {
  private val uiDeviceMock = UIDeviceMock()
  private val deviceUtils = DeviceUtils(uiDeviceMock)

  @Test
  fun `Minimum OS version supported is higher than device os version`() {
    uiDeviceMock.mockSystemVersion = "17.0.1"
    assertFalse(deviceUtils.isOSVersionSupported("17.0.2"))
  }

  @Test
  fun `Minimum OS version supported is equal to device os version`() {
    uiDeviceMock.mockSystemVersion = "16.3.1"
    assertTrue(deviceUtils.isOSVersionSupported("16.3.1"))
  }

  @Test
  fun `Minimum OS version supported is lower than device os version`() {
    uiDeviceMock.mockSystemVersion = "16.3.2"
    assertTrue(deviceUtils.isOSVersionSupported("16.3.1"))
  }
}
