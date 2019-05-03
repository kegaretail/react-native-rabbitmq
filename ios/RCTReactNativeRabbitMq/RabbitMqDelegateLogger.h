#import <React/RCTBridgeModule.h>

#import "RMQConnectionDelegate.h"
#import "EventEmitter.h"

@interface RabbitMqDelegateLogger : NSObject <RMQConnectionDelegate>

    - (nonnull id) init;

@end
