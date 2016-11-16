#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#import "RMQConnectionDelegate.h"

@interface RabbitMqDelegateLogger : NSObject <RMQConnectionDelegate>

    - (nonnull id) initWithBridge:(nonnull RCTBridge *)bridge;

@end