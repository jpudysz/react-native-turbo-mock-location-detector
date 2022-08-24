#import <React/RCTBridgeModule.h>
#import "CoreLocation/CoreLocation.h"

@interface MockLocationDetector : NSObject <RCTBridgeModule, CLLocationManagerDelegate>
    @property CLLocationManager *locationManager;
    @property RCTPromiseResolveBlock resolve;
    @property RCTPromiseRejectBlock reject;
@end
