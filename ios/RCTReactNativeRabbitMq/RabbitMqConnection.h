
#ifdef OLDER_IMPORT
    #import "RCTBridge.h"
    #import "RCTEventDispatcher.h"
#else
    #import <React/RCTBridge.h>
    #import <React/RCTEventDispatcher.h>
#endif

#import <RMQClient/RMQClient.h>
#import <RMQClient/RMQChannel.h>
#import "RabbitMqQueue.h"
#import "RabbitMqDelegateLogger.h"

@interface RabbitMqConnection : NSObject <RCTBridgeModule>

@end