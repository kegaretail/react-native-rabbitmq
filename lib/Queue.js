import {DeviceEventEmitter} from 'react-native';

export class Queue {

    constructor(connection, queue_condig, args) {

        this.callbacks = {};
        this.rabbitmqconnection = connection.rabbitmqconnection;

        this.name = queue_condig.name;
        this.queue_condig = queue_condig;
        this.arguments = args || {};

        this.message_buffer = [];
        this.message_buffer_delay =  (queue_condig.buffer_delay ? queue_condig.buffer_delay : 1000);
        this.message_buffer_timeout = null;

        DeviceEventEmitter.addListener('RabbitMqQueueEvent', this.handleEvent.bind(this));
        
        this.rabbitmqconnection.addQueue(queue_condig, this.arguments);
    }

    handleEvent(event){

        if (event.queue_name != this.name){ return; }

        if (event.name == 'message'){

            if (this.callbacks.hasOwnProperty(event.name)){
                this.callbacks['message'](event);
            }

            if (this.callbacks.hasOwnProperty('messages')){

                this.message_buffer.push(event);

                clearTimeout(this.message_buffer_timeout);

                this.message_buffer_timeout = setTimeout(() => { 
                    if (this.message_buffer.length > 0){
                        this.callbacks['messages'](this.message_buffer);
                        this.message_buffer = [];
                    }
                }, this.message_buffer_delay);

            }

        }else if (this.callbacks.hasOwnProperty(event.name)){
            this.callbacks[event.name](event);
        }

    }

    on(event, callback){
        this.callbacks[event] = callback;
    } 

    removeon(event){
        delete this.callbacks[event];
    } 

    bind(exchange, routing_key = ''){
        this.rabbitmqconnection.bindQueue(exchange.name, this.name, routing_key);    
    }

    unbind(exchange, routing_key = ''){
        this.rabbitmqconnection.unbindQueue(exchange.name, this.name, routing_key);    
    }

}

export default Queue;