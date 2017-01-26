//DO NO REMOVE THIS LINE IF YOU'RE USING IT ON RN > 0.40 PROJECT
//#define OLDER_IMPORT

#ifdef OLDER_IMPORT
    #import "RCTBridge.h"
#else
    #import <React/RCTBridge.h>
#endif

@interface ReactNativeRabbitMq : NSObject <RCTBridgeModule>

@end
