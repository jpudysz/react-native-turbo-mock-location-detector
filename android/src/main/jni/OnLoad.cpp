#include <fbjni/fbjni.h>

#include "MockLocationDetectorComponentsRegistry.h"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
  return facebook::jni::initialize(vm, [] {
    facebook::react::MockLocationDetectorComponentsRegistry::registerNatives();
  });
}
