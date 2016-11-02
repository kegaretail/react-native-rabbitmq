#import "RCTReactNativeRabbitMq.h"

@implementation ReactNativeRabbitMq

RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(test,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve(@"Hello World!");
}

@end
