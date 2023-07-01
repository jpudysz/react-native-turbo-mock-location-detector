#import "MockLocationRequest.h"

@implementation MockLocationRequest

- (id)initWithPromise:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    self = [super init];
    if (self) {
        _m_resolve = resolve;
        _m_reject = reject;
    }
    return self;
}

@end
