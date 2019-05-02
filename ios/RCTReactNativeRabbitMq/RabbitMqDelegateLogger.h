
#ifdef OLDER_IMPORT
    #import "RCTBridge.h"
    #import "RCTEventDispatcher.h"
#else
    #import <React/RCTBridgeModule.h>
    #import <React/RCTEventDispatcher.h>
#endif

#import "RMQConnectionDelegate.h"

@interface RabbitMqDelegateLogger : NSObject <RMQConnectionDelegate>

    - (nonnull id) initWithBridge:(nonnull RCTBridge *)bridge;

@end
