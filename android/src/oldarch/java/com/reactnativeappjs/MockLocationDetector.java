package com.reactnativemocklocationdetector;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MockLocationDetector extends ReactContextBaseJavaModule {
    private final ReactApplicationContext context;
    private final FusedLocationProviderClient locationProviderClient;

    public MockLocationDetector(ReactApplicationContext context) {
        super(context);
        this.context = context;
        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @NonNull
    @Override
    public String getName() {
        return MockLocationDetectorImpl.NAME;
    }

    @Override
    public void isMockingLocation(Promise promise) {
        MockLocationDetectorImpl.isMockingLocation(
            this.context,
            this.locationProviderClient,
            promise
        );
    }
}
