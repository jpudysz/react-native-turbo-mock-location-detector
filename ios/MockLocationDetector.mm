#import "MockLocationDetector.h"
#import <CoreLocation/CoreLocation.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNMockLocationDetectorSpec.h"
#endif

@implementation MockLocationDetector

RCT_EXPORT_MODULE()

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.locationManager = [[CLLocationManager alloc] init];
        self.locationManager.delegate = self;
        
        self.locationManager.distanceFilter = kCLDistanceFilterNone;
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    }
    return self;
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations {
    CLLocation *currentLocation = [locations objectAtIndex:0];
    [self.locationManager stopUpdatingLocation];
    
    if (@available(iOS 15, *)) {
        CLLocationSourceInformation *sourceInformation = [currentLocation sourceInformation];
        NSDictionary *dict = @{
            @"isLocationMocked": @(sourceInformation.isSimulatedBySoftware)
        };
        
        self.resolve(dict);
    } else {
        self.reject(@"error", @"Not available on iOS lower than 15.0", nil);
    }
    
    [self cleanUp];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    self.reject(@"error", @"Location is not available", nil);
    
    [self cleanUp];
}

- (void)cleanUp {
    self.locationManager = nil;
    self.resolve = nil;
    self.reject = nil;
}

- (BOOL)checkIfGPSIsEnabled {
    return [CLLocationManager locationServicesEnabled];
}


RCT_EXPORT_METHOD(isMockingLocation:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    if ([self checkIfGPSIsEnabled] == NO) {
        [[NSException exceptionWithName:@"error"
                      reason:@"Location service is unavailable"
                      userInfo:nil]
                      raise];
    }
        
    self.resolve = resolve;
    self.reject = reject;
    
    [self.locationManager requestLocation];
}

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeMockLocationDetectorSpecJSI>(params);
}
#endif

@end
