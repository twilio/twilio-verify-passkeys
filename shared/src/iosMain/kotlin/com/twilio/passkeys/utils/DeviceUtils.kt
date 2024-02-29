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

package com.twilio.passkeys.utils

import platform.UIKit.UIDevice

internal class DeviceUtils(private val device: UIDevice = UIDevice()) {
  /**
   * Check if current device OS version is supported
   * e.g.
   * deviceOSVersion = 16.0.1, minOSVersionSupported = 16.0.2 should return false
   * deviceOSVersion = 16.0.0, minOSVersionSupported = 15.0.0 should return true
   * deviceOSVersion = 16.3.0, minOSVersionSupported = 16.3.0 should return true
   */
  fun isOSVersionSupported(minOSVersionSupported: String): Boolean {
    val result = device.systemVersion.compareTo(minOSVersionSupported)
    return result >= 0
  }
}
