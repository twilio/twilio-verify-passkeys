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
