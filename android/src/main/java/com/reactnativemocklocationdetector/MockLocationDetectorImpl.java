package com.reactnativemocklocationdetector;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MockLocationDetectorImpl {
    public static final String NAME = "MockLocationDetector";

    public static void isMockingLocation(ReactApplicationContext context, FusedLocationProviderClient locationProviderClient, Promise promise) {
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

        Task<Location> task = locationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                WritableMap result = new WritableNativeMap();
                result.putBoolean("isLocationMocked", location.isFromMockProvider());

                promise.resolve(result);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                promise.reject("2", "Couldn't determine if location is mocked");
            }
        });
    }

    private static boolean checkIfGPSIsEnabled(ReactApplicationContext context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
