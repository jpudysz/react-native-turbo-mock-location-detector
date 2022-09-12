package com.reactnativemocklocationdetector;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

public class MockLocationRequest {
    private final Promise _promise;
    private final LocationManager _locationManager;
    private final String _provider;
    private final Handler _handler = new Handler();
    private boolean _resolved = false;

    public MockLocationRequest(LocationManager locationManager, String provider, Promise promise) {
        this._promise = promise;
        this._locationManager = locationManager;
        this._provider = provider;
    }

    private final Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (MockLocationRequest.this) {
                if (!_resolved) {
                    _resolved = true;
                    _promise.reject("2", "Couldn't determine if location is mocked");
                }
            }
        }
    };

    private final LocationListener LocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            synchronized (MockLocationRequest.this) {
                _locationManager.removeUpdates(MockLocationRequest.this.LocationListener);

                if (_resolved) {
                    return;
                }

                _resolved = true;

                if (location == null) {
                    _promise.reject("2", "Couldn't determine if location is mocked");

                    return;
                }

                _promise.resolve(MockLocationRequest.prepareResult(location.isFromMockProvider()));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @SuppressLint("MissingPermission")
    public void getLocation() {
        this._handler.postDelayed(timeoutRunnable, 10 * 1000);
        this._locationManager.requestLocationUpdates(this._provider, 1000, 0, this.LocationListener);
    }

    public static WritableMap prepareResult(boolean isMocked) {
        WritableMap result = new WritableNativeMap();
        result.putBoolean("isLocationMocked", isMocked);

        return result;
    }
}
