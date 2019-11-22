package com.norma.abc.exception

import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import java.util.*

class BleScanException : BleException {

    /**
     * Returns the reason code of scan failure.
     *
     * @return One of the [Reason] codes.
     */
    @Reason
    @get:Reason
    val reason: Int

    /**
     * Returns a [Date] suggestion when a particular [Reason] should no longer be valid
     *
     * @return the date suggestion or null if no suggestion available
     */
    @get:Nullable
    val retryDateSuggestion: Date?

    @IntDef(
        BLUETOOTH_CANNOT_START,
        BLUETOOTH_DISABLED,
        BLUETOOTH_NOT_AVAILABLE,
        LOCATION_PERMISSION_MISSING,
        LOCATION_SERVICES_DISABLED,
        SCAN_FAILED_ALREADY_STARTED,
        SCAN_FAILED_APPLICATION_REGISTRATION_FAILED,
        SCAN_FAILED_INTERNAL_ERROR,
        SCAN_FAILED_FEATURE_UNSUPPORTED,
        SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES,
        UNDOCUMENTED_SCAN_THROTTLE,
        UNKNOWN_ERROR_CODE
    )

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Reason{}

    constructor(@Reason reason: Int) : super(createMessage(reason, null)) {
        this.reason = reason
        this.retryDateSuggestion = null
    }

    constructor(@Reason reason: Int, @NonNull retryDateSuggestion: Date) : super(createMessage(reason, retryDateSuggestion)) {
        this.reason = reason
        this.retryDateSuggestion = retryDateSuggestion
    }

    constructor(@Reason reason: Int, causeException: Throwable) : super(createMessage(reason, null), causeException) {
        this.reason = reason
        this.retryDateSuggestion = null
    }

    companion object {

        /**
         * Scan did not start correctly because of unspecified error.
         */
        const val BLUETOOTH_CANNOT_START = 0

        /**
         * Scan did not start correctly because the Bluetooth adapter was disabled. Ask the user to turn on Bluetooth or use
         * **android.bluetooth.adapter.action.REQUEST_ENABLE**
         */
        const val BLUETOOTH_DISABLED = 1

        /**
         * Scan did not start correctly because the device does not support it.
         */
        const val BLUETOOTH_NOT_AVAILABLE = 2

        /**
         * Scan did not start correctly because the user did not accept access to location services. On Android 6.0 and up you must ask the
         * user about **ACCESS_COARSE_LOCATION** in runtime.
         */
        const val LOCATION_PERMISSION_MISSING = 3

        /**
         * Scan did not start because location services are disabled on the device. On Android 6.0 and up location services must be enabled
         * in order to receive BLE scan results.
         */
        const val LOCATION_SERVICES_DISABLED = 4

        /**
         * Fails to start scan as BLE scan with the same settings is already started by the app. Only on API >=21.
         */
        const val SCAN_FAILED_ALREADY_STARTED = 5

        /**
         * Fails to start scan as app cannot be registered. Only on API >=21.
         */
        const val SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 6

        /**
         * Fails to start scan due an internal error. Only on API >=21.
         */
        const val SCAN_FAILED_INTERNAL_ERROR = 7

        /**
         * Fails to start power optimized scan as this feature is not supported. Only on API >=21.
         */
        const val SCAN_FAILED_FEATURE_UNSUPPORTED = 8

        /**
         * Fails to start scan as it is out of hardware resources. Only on API >=21.
         */
        const val SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES = 9

        /**
         * On API >=25 there is an undocumented scan throttling mechanism. If 5 scans were started by the app during a 30 second window
         * the next scan in that window will be silently skipped with only a log warning. In this situation there should be
         * a retryDateSuggestion [Date] set with a time when the scan should work again.
         *
         * @link https://blog.classycode.com/undocumented-android-7-ble-behavior-changes-d1a9bd87d983
         */
        const val UNDOCUMENTED_SCAN_THROTTLE = Integer.MAX_VALUE - 1

        /**
         * Unknown error code. Only on API >=21.
         */
        const val UNKNOWN_ERROR_CODE = Integer.MAX_VALUE

        private fun createMessage(reason: Int, retryDateSuggestion: Date?): String {
            return reasonDescription(reason) + " (code " + reason + ")" + retryDateSuggestionIfExists(
                retryDateSuggestion
            )
        }

        private fun reasonDescription(reason: Int): String {
            when (reason) {
                BLUETOOTH_CANNOT_START -> return "Bluetooth cannot start"
                BLUETOOTH_DISABLED -> return "Bluetooth disabled"
                BLUETOOTH_NOT_AVAILABLE -> return "Bluetooth not available"
                LOCATION_PERMISSION_MISSING -> return "Location Permission missing"
                LOCATION_SERVICES_DISABLED -> return "Location Services disabled"
                SCAN_FAILED_ALREADY_STARTED -> return "Scan failed because it has already started"
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> return "Scan failed because application registration failed"
                SCAN_FAILED_INTERNAL_ERROR -> return "Scan failed because of an internal error"
                SCAN_FAILED_FEATURE_UNSUPPORTED -> return "Scan failed because feature unsupported"
                SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> return "Scan failed because out of hardware resources"
                UNDOCUMENTED_SCAN_THROTTLE -> return "Undocumented scan throttle"
                UNKNOWN_ERROR_CODE -> return "Unknown error"
                // fallthrough
                else -> return "Unknown error"
            }
        }

        private fun retryDateSuggestionIfExists(retryDateSuggestion: Date?): String {
            return if (retryDateSuggestion == null) {
                ""
            } else {
                ", suggested retry date is $retryDateSuggestion"
            }
        }
    }
}