package com.reactnativemocklocationdetector

import android.content.Context
import android.location.Criteria
import android.location.LocationManager

import android.Manifest;
import android.os.Build
import androidx.core.content.PermissionChecker
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.common.SystemClock

class MockLocationDetectorModule internal constructor(context: ReactApplicationContext) : MockLocationDetectorSpec(context) {
    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "MockLocationDetector"
    }

    @Suppress("DEPRECATION")
    @ReactMethod()
    override fun isMockingLocation(promise: Promise) {
        if (!this.checkIfGPSIsEnabled()) {
            promise.reject("0", "GPS is not enabled")

            return
        }

        val hasCoarseLocationPermission = PermissionChecker.checkSelfPermission(
            this.reactApplicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED

        val hasFineLocationPermission = PermissionChecker.checkSelfPermission(
            this.reactApplicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED

        if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
            promise.reject("1", "You have no permission to access location")

            return
        }

        val locationManager = this.reactApplicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider: String? = locationManager.getBestProvider(Criteria(), true)

        if (provider == null) {
            promise.reject("2", "Couldn't determine if location is mocked")

            return
        }

        val location = locationManager.getLastKnownLocation(provider)

        if (location == null || SystemClock.currentTimeMillis() - location.time >= 5 * 1000) {
            MockLocationRequest(locationManager, provider, promise).getLocation()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return promise.resolve(MockLocationRequest.prepareResult(location.isMock))
        }

        promise.resolve(MockLocationRequest.prepareResult(location.isFromMockProvider))
    }

    private fun checkIfGPSIsEnabled(): Boolean {
        val manager = this.reactApplicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
