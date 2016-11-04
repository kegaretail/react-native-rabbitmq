#import "RCTReactNativeRabbitMq.h"

@implementation ReactNativeRabbitMq

RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(test2,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve(@"Hello World!");
}

RCT_EXPORT_METHOD(test:(RCTResponseSenderBlock) callback)
{

    NSString* someString = @"something";

    callback(@[someString]);

}

@end
