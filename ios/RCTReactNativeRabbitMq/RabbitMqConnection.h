#import <React/RCTBridgeModule.h>

#import <RMQClient/RMQClient.h>
#import <RMQClient/RMQChannel.h>

#import "RabbitMqQueue.h"
#import "RabbitMqDelegateLogger.h"
#import "EventEmitter.h"

@interface RabbitMqConnection : NSObject <RCTBridgeModule>

@end
