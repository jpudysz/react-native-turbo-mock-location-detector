package com.reactnativemocklocationdetector;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class MockLocationDetector extends ReactContextBaseJavaModule {
    private final ReactApplicationContext context;

    public MockLocationDetector(ReactApplicationContext context) {
        super(context);
        this.context = context;
    }

    @NonNull
    @Override
    public String getName() {
        return MockLocationDetectorImpl.NAME;
    }

    @ReactMethod()
    public void isMockingLocation(Promise promise) {
        MockLocationDetectorImpl.isMockingLocation(this.context, promise);
    }
}
