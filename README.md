
## Installation

## IOS

npm install react-native-rabbitmq --save

 Installation with CocoaPods

1. In the podfile uncommend "use_frameworks":

```
use_frameworks!
```
1. Add the following to your Podfile:

```
pod 'RMQClient', '~> 0.10.0'
```
1. Instal the cocapods:

```
pod install
```



In xcode add a recursive Header Search Path:
```
$(SRCROOT)/Pods
```


You need to change 2 lines, to make it work:

ios\Pods\RMQClient\RMQClient\RMQValues.h Line 53
```
@import JKVValue;
```
to
```
#import "JKVValue.h"
```

ios\Pods\RMQClient\RMQClient\RMQTCPSocketTransport.h
```
@import CocoaAsyncSocket;
```
to
```
#import "GCDAsyncSocket.h"
```

react-native link


## Android 

npm install react-native-rabbitmq --save

react-native link
