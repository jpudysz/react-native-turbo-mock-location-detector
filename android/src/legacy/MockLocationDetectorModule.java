package com.reactnativemocklocationdetector;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class MockLocationDetectorModule extends ReactContextBaseJavaModule {
  public static final String NAME = MockLocationDetectorModuleImpl.NAME;

  MockLocationDetectorModule(ReactApplicationContext context) {
    super(context);
  }

  @Override
  @NonNull
  public String getName() {
    return MockLocationDetectorModuleImpl.NAME;
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    MockLocationDetectorModuleImpl.multiply(a, b, promise);
  }
}
