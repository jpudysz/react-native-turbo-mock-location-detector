#include "MockLocationDetectorComponentsRegistry.h"

#include <CoreComponentsRegistry.h>
#include <fbjni/fbjni.h>
#include <react/renderer/componentregistry/ComponentDescriptorProviderRegistry.h>
#include <react/renderer/components/rncore/ComponentDescriptors.h>
#include <react/renderer/components/reactnativemocklocationdetector/ComponentDescriptors.h>

namespace facebook {
namespace react {

MockLocationDetectorComponentsRegistry::MockLocationDetectorComponentsRegistry(
    ComponentFactory *delegate)
    : delegate_(delegate) {}

std::shared_ptr<ComponentDescriptorProviderRegistry const>
MockLocationDetectorComponentsRegistry::sharedProviderRegistry() {
  auto providerRegistry = CoreComponentsRegistry::sharedProviderRegistry();

  return providerRegistry;
}

jni::local_ref<MockLocationDetectorComponentsRegistry::jhybriddata>
MockLocationDetectorComponentsRegistry::initHybrid(
    jni::alias_ref<jclass>,
    ComponentFactory *delegate) {
  auto instance = makeCxxInstance(delegate);

  auto buildRegistryFunction =
      [](EventDispatcher::Weak const &eventDispatcher,
         ContextContainer::Shared const &contextContainer)
      -> ComponentDescriptorRegistry::Shared {
    auto registry = MockLocationDetectorComponentsRegistry::sharedProviderRegistry()
                        ->createComponentDescriptorRegistry(
                            {eventDispatcher, contextContainer});

    auto mutableRegistry =
        std::const_pointer_cast<ComponentDescriptorRegistry>(registry);

    mutableRegistry->setFallbackComponentDescriptor(
        std::make_shared<UnimplementedNativeViewComponentDescriptor>(
            ComponentDescriptorParameters{
                eventDispatcher, contextContainer, nullptr}));

    return registry;
  };

  delegate->buildRegistryFunction = buildRegistryFunction;
  return instance;
}

void MockLocationDetectorComponentsRegistry::registerNatives() {
  registerHybrid({
      makeNativeMethod("initHybrid", MockLocationDetectorComponentsRegistry::initHybrid),
  });

  // This is a temporary solution that allows components exported by react-native-mock-location-detector
  // library to be added to the main component registry. This code is triggered
  // when c++ react-native-mock-location-detector library is initialized and is needed because RN's autolinking
  // does not currently support Fabric components. As a consequence, users would need
  // to manually put library initialization calls in their ReactNativeHost implementation
  // which is undesirable.
  sharedProviderRegistry();
}

} // namespace react
} // namespace facebook
