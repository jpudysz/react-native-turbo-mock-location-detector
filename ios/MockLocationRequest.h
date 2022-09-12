#import <React/RCTBridgeModule.h>

#ifndef MockLocationRequest_h
#define MockLocationRequest_h

@interface MockLocationRequest: NSObject

@property (nonatomic, copy)RCTPromiseResolveBlock m_resolve;
@property (nonatomic, copy)RCTPromiseRejectBlock m_reject;

-(id) initWithPromise:(RCTPromiseResolveBlock) resolve reject:(RCTPromiseRejectBlock) reject;

@end

#endif
