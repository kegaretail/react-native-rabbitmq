import {DeviceEventEmitter} from 'react-native';

export class Queue {

    constructor(connection, queue_condig, args) {

        this.callbacks = {};
        this.rabbitmqconnection = connection.rabbitmqconnection;

        this.name = queue_condig.name;
        this.queue_condig = queue_condig;
        this.arguments = args || {};

        DeviceEventEmitter.addListener('RabbitMqQueueEvent', this.handleEvent.bind(this));
        
        this.rabbitmqconnection.addQueue(queue_condig, this.arguments);
    }

    handleEvent(event){
        if (event.queue_name == this.name && this.callbacks.hasOwnProperty(event.name)){
            this.callbacks[event.name](event)
        }
    }

    on(event, callback){
        this.callbacks[event] = callback;
    } 

    bind(exchange, routing_key = ''){
        console.log('[Queue] Bind');
        this.rabbitmqconnection.bindQueue(exchange.name, this.name, routing_key);    
    }

    unbind(exchange, routing_key = ''){
        console.log('[Queue] UnBind');

        this.rabbitmqconnection.unbindQueue(exchange.name, this.name, routing_key);    
    }

    /*
    publish(message, routing_key){

        console.log('[Queue] Send: ' + message + ' To: ' + this.name);

        this.rabbitmqconnection.publishToQueue(message, this.exchange_condig.name, routing_key);    
    }
    */
}

export default Queue;