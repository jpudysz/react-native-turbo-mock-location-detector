package com.reactnativemocklocationdetector;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;

public class MockLocationDetectorImpl {
    private static int samplesCount = 0;
    private static int mockedSampleNo = -1;
    private static Location lastMockedLocation;
    private static Promise _promise;
    public static final String NAME = "MockLocationDetector";

    public static void isMockingLocation(ReactApplicationContext context, FusedLocationProviderClient locationProviderClient, Promise promise) {
        MockLocationDetectorImpl._promise = promise;

        if (!MockLocationDetectorImpl.checkIfGPSIsEnabled(context)) {
            MockLocationDetectorImpl._promise.reject("0", "GPS is not enabled");
            MockLocationDetectorImpl.cleanAll();

            return;
        }

        boolean hasCoarseLocationPermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED;
        boolean hasFineLocationPermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED;

        if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
            MockLocationDetectorImpl._promise.reject("1", "You have no permission to access location");

            return;
        }

        LocationRequest locationRequest = LocationRequest
            .create()
            .setWaitForAccurateLocation(true)
            .setNumUpdates(5)
            .setInterval(500)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.i("MOCK LOCATION DETECTOR", String.format("Sample no=%d", MockLocationDetectorImpl.samplesCount + 1));

                // last iteration
                if (MockLocationDetectorImpl.samplesCount == 4) {
                    WritableMap result = new WritableNativeMap();
                    result.putBoolean("isLocationMocked", MockLocationDetectorImpl.lastMockedLocation != null);

                    MockLocationDetectorImpl._promise.resolve(result);
                    MockLocationDetectorImpl.cleanAll();

                    return;
                }

                MockLocationDetectorImpl.samplesCount += 1;
                Location lastLocation = locationResult.getLastLocation();

                if (lastLocation == null) {
                    return;
                }

                Log.i("MOCK LOCATION DETECTOR", String.format("isLocationMocked=%s", lastLocation.isFromMockProvider()));

                if (lastLocation.isFromMockProvider()) {
                    MockLocationDetectorImpl.lastMockedLocation = lastLocation;
                    MockLocationDetectorImpl.mockedSampleNo = MockLocationDetectorImpl.samplesCount;

                    return;
                }

                // we didn't detect mocked location yet, and new location is not mocked
                if (MockLocationDetectorImpl.lastMockedLocation == null) {
                    return;
                }

                // we detected mocked location, but new location is not mocked
                float distanceInMeters = MockLocationDetectorImpl.lastMockedLocation.distanceTo(lastLocation);

                // location is further than 1km, and we passed at least 2 iterations
                if (distanceInMeters > 1000.0f || MockLocationDetectorImpl.samplesCount - MockLocationDetectorImpl.mockedSampleNo >= 2) {
                    MockLocationDetectorImpl.mockedSampleNo = -1;
                    MockLocationDetectorImpl.lastMockedLocation = null;
                }
            }
        };

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationProviderClient.removeLocationUpdates(callback);

                if (MockLocationDetectorImpl._promise == null) {
                    // promise was already called
                    return;
                }

                if (MockLocationDetectorImpl.samplesCount == 0) {
                    MockLocationDetectorImpl._promise.reject("2", "Couldn't determine if location is mocked");
                    MockLocationDetectorImpl.cleanAll();

                    return;
                }

                WritableMap result = new WritableNativeMap();
                result.putBoolean("isLocationMocked", MockLocationDetectorImpl.lastMockedLocation != null);

                MockLocationDetectorImpl._promise.resolve(result);
                MockLocationDetectorImpl.cleanAll();
            }
        }, 10000);

        locationProviderClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    private static void cleanAll() {
        MockLocationDetectorImpl.samplesCount = 0;
        MockLocationDetectorImpl.mockedSampleNo = -1;
        MockLocationDetectorImpl.lastMockedLocation = null;
        MockLocationDetectorImpl._promise = null;
    }

    private static boolean checkIfGPSIsEnabled(ReactApplicationContext context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
