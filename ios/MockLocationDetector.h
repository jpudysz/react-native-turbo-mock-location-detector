#import <React/RCTBridgeModule.h>
#import "CoreLocation/CoreLocation.h"
#import "MockLocationRequest.h"

@interface MockLocationDetector : NSObject <RCTBridgeModule, CLLocationManagerDelegate>
    @property CLLocationManager *m_locationManager;
    @property NSMutableArray<MockLocationRequest *> *m_requests;
@end
