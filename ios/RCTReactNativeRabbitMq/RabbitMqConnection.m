#import "RabbitMqConnection.h"

@interface RabbitMqConnection ()
    @property (nonatomic, readwrite) NSDictionary *config;
    @property (nonatomic, readwrite) RMQConnection *connection;
    @property (nonatomic, readwrite) id<RMQChannel> channel;
    @property (nonatomic, readwrite) bool connected;
    @property (nonatomic, readwrite) NSMutableArray *queues;
    @property (nonatomic, readwrite) NSMutableArray *exchanges;
@end

@implementation RabbitMqConnection

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(initialize:(NSDictionary *) config)
{
    self.config = config;

    self.connected = false;

    self.queues = [[NSMutableArray alloc] init];
    self.exchanges = [[NSMutableArray alloc] init];
}

RCT_EXPORT_METHOD(connect)
{

    RabbitMqDelegateLogger *delegate = [[RabbitMqDelegateLogger alloc] initWithBridge:self.bridge];

    NSString *uri = [NSString stringWithFormat:@"amqp://%@:%@@%@:%@/%@", self.config[@"username"], self.config[@"password"], self.config[@"host"], self.config[@"port"], self.config[@"virtualhost"]];
    //self.connection = [[RMQConnection alloc] initWithUri:uri verifyPeer:true delegate:delegate];

    self.connection = [[RMQConnection alloc] initWithUri:uri 
                                              channelMax:@65535 
                                                frameMax:@(RMQFrameMax) 
                                               heartbeat:@10
                                             syncTimeout:@10 
                                                delegate:delegate
                                           delegateQueue:dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)];

    [self.connection start:^{ 
        
        self.connected = true;

        [self.bridge.eventDispatcher sendAppEventWithName:@"RabbitMqConnectionEvent" body:@{@"name": @"connected"}];

    }];
}

RCT_EXPORT_METHOD(close)
{
    [self.connection close];
}

RCT_EXPORT_METHOD(addQueue:(NSDictionary *) config arguments:(NSDictionary *)arguments)
{
    if (self.connected){ 
        self.channel = [self.connection createChannel];

        RMQQueueDeclareOptions queue_options = RMQQueueDeclareDurable;
        RabbitMqQueue *queue = [[RabbitMqQueue alloc] initWithConfig:config channel:self.channel bridge:self.bridge];

        [self.queues addObject:queue];
    }
}

RCT_EXPORT_METHOD(bindQueue:(NSString *)exchange_name queue_name:(NSString *)queue_name routing_key:(NSString *)routing_key)
{

    id queue_id = [self findQueue:queue_name];
    id exchange_id = [self findExchange:exchange_name];

    if (queue_id != nil && exchange_id != nil){
        [queue_id bind:exchange_id routing_key:routing_key];
    }
    
}

RCT_EXPORT_METHOD(unbindQueue:(NSString *)exchange_name queue_name:(NSString *)queue_name routing_key:(NSString *)routing_key)
{
    id queue_id = [self findQueue:queue_name];
    id exchange_id = [self findExchange:exchange_name];

    if (queue_id != nil && exchange_id != nil){
        [queue_id unbind:exchange_id routing_key:routing_key];
    }
}

RCT_EXPORT_METHOD(removeQueue:(NSString *)queue_name)
{
    id queue_id = [self findQueue:queue_name];

    if (queue_id != nil){
        [queue_id delete];
    }
}


RCT_EXPORT_METHOD(addExchange:(NSDictionary *) config)
{

    RMQExchangeDeclareOptions options = RMQExchangeDeclareNoOptions;
    
    if ([config objectForKey:@"passive"] != nil && [[config objectForKey:@"passive"] boolValue]){
        options = options | RMQExchangeDeclarePassive;
    }

    if ([config objectForKey:@"durable"] != nil && [[config objectForKey:@"durable"] boolValue]){
        options = options | RMQExchangeDeclareDurable;
    }

    if ([config objectForKey:@"autoDelete"] != nil && [[config objectForKey:@"autoDelete"] boolValue]){
        options = options | RMQExchangeDeclareAutoDelete;
    }

    if ([config objectForKey:@"internal"] != nil && [[config objectForKey:@"internal"] boolValue]){
        options = options | RMQExchangeDeclareInternal;
    }

    if ([config objectForKey:@"NoWait"] != nil && [[config objectForKey:@"NoWait"] boolValue]){
        options = options | RMQExchangeDeclareNoWait;
    }

    
    NSString *type = [config objectForKey:@"type"];

    RMQExchange *exchange = nil;
    if ([type isEqualToString:@"fanout"]) {
        exchange = [self.channel fanout:[config objectForKey:@"name"] options:options];
    }else if ([type isEqualToString:@"direct"]) {
        exchange = [self.channel direct:[config objectForKey:@"name"] options:options];
    }else if ([type isEqualToString:@"topic"]) {
        exchange = [self.channel topic:[config objectForKey:@"name"] options:options];
    }
    
    if (exchange != nil){
        [self.exchanges addObject:exchange];
    }

}

RCT_EXPORT_METHOD(publishToExchange:(NSString *)message exchange_name:(NSString *)exchange_name routing_key:(NSString *)routing_key)
{

    id exchange_id = [self findExchange:exchange_name];

    if (exchange_id != nil){
        NSData* data = [message dataUsingEncoding:NSUTF8StringEncoding];
        [exchange_id publish:data routingKey:routing_key];
    }
}

RCT_EXPORT_METHOD(deleteExchange:(NSString *)exchange_name)
{
   id exchange_id = [self findExchange:exchange_name];

    if (exchange_id != nil){
        [exchange_id delete];
    }
}



-(id) findQueue:(NSString *)name {
    id queue_id = nil;
    for(id q in self.queues) {
        if ([[q name] isEqualToString:name]){ queue_id = q; }
    }
    return queue_id;
}

-(id) findExchange:(NSString *)name {
    id exchange_id = nil;
    for(id e in self.exchanges) {
        if ([[e name] isEqualToString:name]){ exchange_id = e; }
    }

    return exchange_id;
}

@end
