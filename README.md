## Todo

## Installation



## RN 0.60 >

## IOS
npm install react-native-rabbitmq --save

cd ./ios

change in the Podfile line 1:
platform :ios, '9.0' to platform :ios, '10.0'

pod install

## Android

npm install react-native-rabbitmq --save

## RN 0.60 <

## IOS

npm install react-native-rabbitmq --save

 Installation with CocoaPods

1. In the Podfile uncomment "use_frameworks" (Optional):

```
use_frameworks!
```
2. Add the following to your Podfile, use master because needed fix is not a tag:

```
pod 'react-native-rabbitmq', :path => '../node_modules/react-native-rabbitmq'
pod 'RMQClient', :git => 'https://github.com/rabbitmq/rabbitmq-objc-client.git'
```
3. Install the cocapods:

```
pod install
```



In xcode add a recursive Header Search Path:
```
$(SRCROOT)/Pods
```


You need to change some things, to make it work:

ios\Pods\RMQClient\RMQClient\RMQValues.h Line 53
```
@import JKVValue;
```
to
```
#import "JKVValue.h"
```

ios\Pods\JKVValue\JKVValue\Public\JKVValue.h
```
#import <JKVValue/JKVValueImpl.h>
#import <JKVValue/JKVMutableValue.h>
#import <JKVValue/JKVObjectPrinter.h>
#import <JKVValue/JKVFactory.h>
```
to
```
#import "JKVValueImpl.h"
#import "JKVMutableValue.h"
#import "JKVObjectPrinter.h"
#import "JKVFactory.h"
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


## Usage
```
import { Connection, Exchange, Queue } from 'react-native-rabbitmq';

const config = {
	host:'',
	port:5672,
	username:'user',
	password:'password',
	virtualhost:'vhost',
	ttl: 10000 // Message time to live,
	ssl: true // Enable ssl connection, make sure the port is 5671 or an other ssl port
}

let connection = new Connection(config);

connection.on('error', (event) => {

});

connection.on('connected', (event) => {

	let queue = new Queue( this.connection, {
		name: 'queue_name',
		passive: false,
		durable: true,
		exclusive: false,
		consumer_arguments: {'x-priority': 1}
	}, {
	// queueDeclare args here like x-message-ttl
	});

	let exchange = new Exchange(connection, {
		name: 'exchange_name',
		type: 'direct',
		durable: true,
		autoDelete: false,
		internal: false
	});

	queue.bind(exchange, 'queue_name');

	// Receive one message when it arrives
	queue.on('message', (data) => {

	});

	// Receive all messages send with in a second
	queue.on('messages', (data) => {

	});

});

let message = 'test';
let routing_key = '';
let properties = {
	expiration: 10000
}
exchange.publish(data, routing_key, properties)

```
