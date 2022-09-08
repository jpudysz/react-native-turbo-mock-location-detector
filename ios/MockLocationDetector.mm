#import "MockLocationDetector.h"
#import <CoreLocation/CoreLocation.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNMockLocationDetectorSpec.h"
#endif

@implementation MockLocationDetector

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(isMockingLocation:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        if(![self getCachedVersionOrFail:resolve reject:reject]) {
            return;
        }

        if ([self checkIfGPSIsEnabled] == NO) {
            reject(@"0", @"GPS is not enabled", nil);

            return;
        }

        if ([self hasLocationPermission] == NO) {
            reject(@"1", @"You have no permission to access location", nil);

            return;
        }

        self.resolve = resolve;
        self.reject = reject;

        self.locationManager = [[CLLocationManager alloc] init];
        self.locationManager.delegate = self;

        self.locationManager.distanceFilter = kCLDistanceFilterNone;
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest;

        [self.locationManager requestLocation];
    });
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations {
    CLLocation *currentLocation = [locations objectAtIndex:0];
    [self.locationManager stopUpdatingLocation];

    if (@available(iOS 15, *)) {
        CLLocationSourceInformation *sourceInformation = [currentLocation sourceInformation];
        NSDictionary *dict = @{
            @"isLocationMocked": @(sourceInformation.isSimulatedBySoftware)
        };

        self.cachedIsLocationMocked = [NSNumber numberWithBool:sourceInformation.isSimulatedBySoftware];
        self.resolve(dict);
    } else {
        self.reject(@"2", @"Couldn't determine if location is mocked", nil);
    }

    [self cleanUp];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    self.reject(@"2", @"Couldn't determine if location is mocked", nil);

    [self cleanUp];
}

- (void)cleanUp {
    self.locationManager = nil;
    self.resolve = nil;
    self.reject = nil;
    self.cachedIsLocationMocked = nil;
}

- (void)dealloc {
    [self cleanUp];
}

- (BOOL)getCachedVersionOrFail:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    if (!self.resolve && !self.reject) {
        return YES;
    }

    // other promise in progress
    if (self.cachedIsLocationMocked != nil) {
        NSDictionary *dict = @{
            @"isLocationMocked": @([self.cachedIsLocationMocked boolValue])
        };

        resolve(dict);

        return NO;
    }

    reject(@"2", @"Couldn't determine if location is mocked", nil);

    return NO;
}

- (BOOL)checkIfGPSIsEnabled {
    return [CLLocationManager locationServicesEnabled];
}

- (BOOL)hasLocationPermission {
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];

    return status == kCLAuthorizationStatusAuthorizedAlways || status == kCLAuthorizationStatusAuthorizedWhenInUse;
}

+ (BOOL)requiresMainQueueSetup
{
  return YES;
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
