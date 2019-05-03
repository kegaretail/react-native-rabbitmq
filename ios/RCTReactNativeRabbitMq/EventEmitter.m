#import "EventEmitter.h"

@implementation EventEmitter

RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents {
  return @[@"RabbitMqQueueEvent", @"RabbitMqConnectionEvent"];
}


- (void)startObserving {
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    for (NSString *notificationName in [self supportedEvents]) {
        [center addObserver:self
               selector:@selector(emitEventInternal:)
                   name:notificationName
                 object:nil];
    }
}

- (void)stopObserving {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)emitEventInternal:(NSNotification *)notification {
    [self sendEventWithName:notification.name
                   body:notification.userInfo];
}

+ (void)emitEventWithName:(NSString *)name body:(NSDictionary *)body {
    [[NSNotificationCenter defaultCenter] postNotificationName:name
                                                    object:self
                                                  userInfo:body];
}


@end