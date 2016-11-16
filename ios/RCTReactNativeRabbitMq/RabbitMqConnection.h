#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#import <RMQClient/RMQClient.h>
#import <RMQClient/RMQChannel.h>
#import "RabbitMqQueue.h"
#import "RabbitMqDelegateLogger.h"

@interface RabbitMqConnection : NSObject <RCTBridgeModule>

@end