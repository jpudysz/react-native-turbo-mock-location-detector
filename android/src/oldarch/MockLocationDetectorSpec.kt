package com.reactnativemocklocationdetector

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactContextBaseJavaModule

abstract class MockLocationDetectorSpec internal constructor(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
    abstract fun isMockingLocation(promise: Promise)
}
