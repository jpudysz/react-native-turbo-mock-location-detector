package com.reactnativemocklocationdetector;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;

public class MockLocationDetector extends NativeMockLocationDetectorSpec {
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

    @Override
    public void isMockingLocation(Promise promise) {
        MockLocationDetectorImpl.isMockingLocation(this.context, promise);
    }
}
