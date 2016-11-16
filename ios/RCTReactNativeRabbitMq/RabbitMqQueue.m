#import "RabbitMqQueue.h"

@interface RabbitMqQueue ()
    @property (nonatomic, readwrite) NSString *name;
    @property (nonatomic, readwrite) NSDictionary *config;
    @property (nonatomic, readwrite) RMQQueue *queue; 
    @property (nonatomic, readwrite) id<RMQChannel> channel;
    @property (nonatomic, readwrite) RMQQueueDeclareOptions options;
    @property (nonatomic, readwrite) RCTBridge *bridge;
@end

@implementation RabbitMqQueue


RCT_EXPORT_MODULE();

-(id)initWithConfig:(NSDictionary *)config channel:(id<RMQChannel>)channel bridge:(RCTBridge *)bridge {
    if (self = [super init]) {

        self.config = config;
        self.channel = channel;
        self.name = [config objectForKey:@"name"];
        self.bridge = bridge;

        self.options = RMQQueueDeclareNoOptions;
    
        if ([config objectForKey:@"passive"] != nil && [[config objectForKey:@"passive"] boolValue]){
            self.options = self.options | RMQQueueDeclarePassive;
        }

        if ([config objectForKey:@"durable"] != nil && [[config objectForKey:@"durable"] boolValue]){
           self.options = self.options | RMQQueueDeclareDurable;
        }

        if ([config objectForKey:@"exclusive"] != nil && [[config objectForKey:@"exclusive"] boolValue]){
            self.options = self.options | RMQQueueDeclareExclusive;
        }

        if ([config objectForKey:@"autoDelete"] != nil && [[config objectForKey:@"autoDelete"] boolValue]){
            self.options = self.options | RMQExchangeDeclareAutoDelete;
        }

        if ([config objectForKey:@"NoWait"] != nil && [[config objectForKey:@"NoWait"] boolValue]){
            self.options = self.options | RMQQueueDeclareNoWait;
        }

        self.queue = [self.channel queue:self.name options:self.options];

        [self.queue subscribe:^(RMQMessage * _Nonnull message) {

            NSString *body = [[NSString alloc] initWithData:message.body encoding:NSUTF8StringEncoding];

            [self.bridge.eventDispatcher sendAppEventWithName:@"RabbitMqQueueEvent" 
                body:@{
                    @"name": @"message", 
                    @"queue_name": self.name, 
                    @"body": body, 
                    @"routingKey": message.routingKey, 
                    @"exchangeName": message.exchangeName,
                    @"consumerTag": message.consumerTag, 
                    @"deliveryTag": message.deliveryTag
                }
            ];
        }];

    }
    return self;
}

-(void) bind:(RMQExchange *)exchange routing_key:(NSString *)routing_key {

  
    if ([routing_key length] == 0){
        [self.queue bind:exchange];
    }else{
        [self.queue bind:exchange routingKey:routing_key];
    }
}

-(void) unbind:(RMQExchange *)exchange routing_key:(NSString *)routing_key {

    if ([routing_key length] == 0){
        [self.queue unbind:exchange];
    }else{
        [self.queue unbind:exchange routingKey:routing_key];
    }
}

-(void) delete {
    [self.queue delete:self.options];
}



@end