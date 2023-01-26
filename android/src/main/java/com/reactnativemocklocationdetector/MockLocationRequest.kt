package com.reactnativemocklocationdetector

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import kotlinx.coroutines.*

class MockLocationRequest(
    private val _locationManager: LocationManager,
    private val _provider: String,
    private val _promise: Promise
)  {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var _resolved = false

    @Suppress("DEPRECATION")
    private val locationListenerRef: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            with(this@MockLocationRequest) {
                _locationManager.removeUpdates(locationListenerRef)

                if (_resolved) {
                    return
                }

                _resolved = true
                job.cancel()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    return _promise.resolve(prepareResult(location.isMock))
                }

                _promise.resolve(prepareResult(location.isFromMockProvider))
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        scope.launch {
            delay(10000)
            if (!_resolved) {
                _resolved = true
                _promise.reject("2", "Couldn't determine if location is mocked")
            }
        }
        this._locationManager.requestLocationUpdates(this._provider, 1000, 0f, locationListenerRef)
    }

    companion object {
        fun prepareResult(isMocked: Boolean): WritableMap {
            val result = WritableNativeMap()

            result.putBoolean("isLocationMocked", isMocked)

            return result
        }
    }
}
