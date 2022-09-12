#import "MockLocationDetector.h"
#import "MockLocationRequest.h"
#import <CoreLocation/CoreLocation.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNMockLocationDetectorSpec.h"
#endif

@implementation MockLocationDetector

RCT_EXPORT_MODULE()

#pragma mark - Private API

- (instancetype)init
{
    self = [super init];
    if (self) {
        _m_requests = [NSMutableArray new];
    }
    return self;
}

- (void)dealloc {
    [self cleanUp];
}

- (void)cleanUp {
    [self.m_requests removeAllObjects];
    self.m_locationManager = nil;
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

- (BOOL)checkIfGPSIsEnabled {
    return [CLLocationManager locationServicesEnabled];
}

- (BOOL)hasLocationPermission {
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];

    return status == kCLAuthorizationStatusAuthorizedAlways || status == kCLAuthorizationStatusAuthorizedWhenInUse;
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations {
    CLLocation *currentLocation = [locations objectAtIndex:0];

    if (@available(iOS 15, *)) {
        CLLocationSourceInformation *sourceInformation = [currentLocation sourceInformation];
        NSDictionary *dict = @{
            @"isLocationMocked": @(sourceInformation.isSimulatedBySoftware)
        };

        for (MockLocationRequest *request in self.m_requests) {
            request.m_resolve(dict);
        }
    } else {
        for (MockLocationRequest *request in self.m_requests) {
            request.m_reject(@"2", @"Couldn't determine if location is mocked", nil);
        }
    }

    [self cleanUp];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    for (MockLocationRequest *request in self.m_requests) {
        request.m_reject(@"2", @"Couldn't determine if location is mocked", nil);
    }

    [self cleanUp];
}

#pragma mark - Public API

RCT_EXPORT_METHOD(isMockingLocation:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    if ([self checkIfGPSIsEnabled] == NO) {
        reject(@"0", @"GPS is not enabled", nil);

        return;
    }

    if ([self hasLocationPermission] == NO) {
        reject(@"1", @"You have no permission to access location", nil);

        return;
    }

    self.m_locationManager = [CLLocationManager new];

    self.m_locationManager.delegate = self;
    self.m_locationManager.distanceFilter = kCLDistanceFilterNone;
    self.m_locationManager.desiredAccuracy = kCLLocationAccuracyBest;

    MockLocationRequest *request = [[MockLocationRequest alloc] initWithPromise:resolve reject:reject];

    [self.m_requests addObject:request];
    [self.m_locationManager requestLocation];
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
