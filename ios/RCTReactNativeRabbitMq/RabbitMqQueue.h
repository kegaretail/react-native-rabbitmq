#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#import <RMQClient/RMQClient.h>

@interface RabbitMqQueue : NSObject <RCTBridgeModule>

    - (nonnull id) initWithConfig:(nonnull NSDictionary *)config 
                          channel:(nonnull id<RMQChannel>)channel
                           bridge:(nonnull RCTBridge *)bridge;

    - (void) bind:(nonnull RMQExchange *)exchange routing_key:(NSString *)routing_key;
    - (void) unbind:(nonnull RMQExchange *)exchange routing_key:(NSString *)routing_key;
    - (void) delete;
@end