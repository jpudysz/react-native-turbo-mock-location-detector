package com.reactnativemocklocationdetector;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.content.PermissionChecker;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.SystemClock;

public class MockLocationDetectorImpl {
    public static final String NAME = "MockLocationDetector";

    public static void isMockingLocation(ReactApplicationContext context, Promise promise) {
        if (!MockLocationDetectorImpl.checkIfGPSIsEnabled(context)) {
            promise.reject("0", "GPS is not enabled");

            return;
        }

        boolean hasCoarseLocationPermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED;
        boolean hasFineLocationPermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED;

        if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
            promise.reject("1", "You have no permission to access location");

            return;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null && (SystemClock.currentTimeMillis() - location.getTime() < 5 * 1000)) {
            promise.resolve(MockLocationRequest.prepareResult(location.isFromMockProvider()));

            return;
        }

        new MockLocationRequest(locationManager, provider, promise)
            .getLocation();
    }

    private static boolean checkIfGPSIsEnabled(ReactApplicationContext context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
