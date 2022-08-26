package com.reactnativemocklocationdetector;

import com.facebook.jni.HybridData;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.fabric.ComponentFactory;
import com.facebook.soloader.SoLoader;

@DoNotStrip
public class MockLocationDetectorComponentsRegistry {
    static {
        SoLoader.loadLibrary("fabricjni");
        SoLoader.loadLibrary("reactnativemocklocationdetector_modules");
    }

    @DoNotStrip private final HybridData mHybridData;

    @DoNotStrip
    private native HybridData initHybrid(ComponentFactory componentFactory);

    @DoNotStrip
    private MockLocationDetectorComponentsRegistry(ComponentFactory componentFactory) {
        mHybridData = initHybrid(componentFactory);
    }

    @DoNotStrip
    public static MockLocationDetectorComponentsRegistry register(ComponentFactory componentFactory) {
        return new MockLocationDetectorComponentsRegistry(componentFactory);
    }
}
