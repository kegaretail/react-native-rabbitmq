#import "RabbitMqDelegateLogger.h"

@implementation RabbitMqDelegateLogger

-(id)init {
    self = [super init];
    return self;
}

- (void)connection:(RMQConnection *)connection failedToConnectWithError:(NSError *)error {
    NSLog(@"RabbitMq Received connection: %@ failedToConnectWithError: %@", connection, error);
    [EventEmitter emitEventWithName:@"RabbitMqConnectionEvent" body:@{@"name": @"error", @"type": @"failedtoconnect", @"code": [NSString stringWithFormat:@"%ld", error.code], @"description": [error localizedDescription]}];
}

- (void)connection:(RMQConnection *)connection disconnectedWithError:(NSError *)error {
    NSLog(@"RabbitMq Received connection: %@ disconnectedWithError: %@", connection, error);
    [EventEmitter emitEventWithName:@"RabbitMqConnectionEvent" body:@{@"name": @"error", @"type": @"disconnected",  @"code": [NSString stringWithFormat:@"%ld", error.code], @"description": [error localizedDescription]}];
}

- (void)channel:(id<RMQChannel>)channel error:(NSError *)error {
    NSLog(@"RabbitMq Received channel: %@ error: %@", channel, error);
    [EventEmitter emitEventWithName:@"RabbitMqConnectionEvent" body:@{@"name": @"error", @"type": @"channel",  @"code": [NSString stringWithFormat:@"%ld", error.code], @"description": [error localizedDescription]}];
}

- (void)willStartRecoveryWithConnection:(RMQConnection *)connection {
    NSLog(@"RabbitMq Will start recovery for connection: %@", connection);
}

- (void)startingRecoveryWithConnection:(RMQConnection *)connection {
    NSLog(@"RabbitMq Starting recovery for connection: %@", connection);
}

- (void)recoveredConnection:(RMQConnection *)connection {
    NSLog(@"RabbitMq Recovered connection: %@", connection);
    [EventEmitter emitEventWithName:@"RabbitMqConnectionEvent" body:@{@"name": @"reconnected"}];
}

@end