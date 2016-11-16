#import "RabbitMqDelegateLogger.h"

@interface RabbitMqDelegateLogger ()
    @property (nonatomic, readwrite) RCTBridge *bridge;
@end

@implementation RabbitMqDelegateLogger

-(id)initWithBridge:(RCTBridge *)bridge {
    
    if (self = [super init]) {
        self.bridge = bridge;
    }
    return self;
}

- (void)connection:(RMQConnection *)connection failedToConnectWithError:(NSError *)error {
    NSLog(@"Received connection ------------: %@ failedToConnectWithError: %@", connection, error);
    [self.bridge.eventDispatcher sendAppEventWithName:@"RabbitMqConnectionEvent" body:@{@"name": @"error", @"type": @"failedtoconnect", @"code": [NSString stringWithFormat:@"%ld", error.code], @"description": [error localizedDescription]}];
}

- (void)connection:(RMQConnection *)connection disconnectedWithError:(NSError *)error {
    NSLog(@"Received connection------------: %@ disconnectedWithError: %@", connection, error);
    [self.bridge.eventDispatcher sendAppEventWithName:@"RabbitMqConnectionEvent" body:@{@"name": @"error", @"type": @"disconnected",  @"code": [NSString stringWithFormat:@"%ld", error.code], @"description": [error localizedDescription]}];
}

- (void)channel:(id<RMQChannel>)channel error:(NSError *)error {
    NSLog(@"Received channel------------: %@ error: %@", channel, error);
    [self.bridge.eventDispatcher sendAppEventWithName:@"RabbitMqConnectionEvent" body:@{@"name": @"error", @"type": @"channel",  @"code": [NSString stringWithFormat:@"%ld", error.code], @"description": [error localizedDescription]}];
}

- (void)willStartRecoveryWithConnection:(RMQConnection *)connection {
    NSLog(@"Will start recovery for connection------------: %@", connection);
}

- (void)startingRecoveryWithConnection:(RMQConnection *)connection {
    NSLog(@"Starting recovery for connection------------: %@", connection);
}

- (void)recoveredConnection:(RMQConnection *)connection {
    NSLog(@"Recovered connection------------: %@", connection);
}

@end