import {NativeModules, DeviceEventEmitter} from 'react-native';

const RabbitMqConnection = NativeModules.RabbitMqConnection;

export class Exchange {

    constructor(exchange_condig) {

        this.callbacks = {};
        this.rabbitmqconnection = RabbitMqConnection;

        this.name = exchange_condig.name;
        this.exchange_condig = exchange_condig;

        this.rabbitmqconnection.addExchange(exchange_condig);

        DeviceEventEmitter.addListener('RabbitMqExchangeEvent', this.handleEvent.bind(this));

    }

    handleEvent(event){
        if (event.queue_name == this.name && this.callbacks.hasOwnProperty(event.name)){
            this.callbacks[event.name](event)
        }
    }

    on(event, callback){
        this.callbacks[event] = callback;
    } 
    
    publish(message, routing_key = ''){

        console.log('[Exchange] Send: ' + message + ' To: ' + this.name);

        this.rabbitmqconnection.publishToExchange(message, this.name, routing_key);    
    }

    delete(if_unused = false){
        this.rabbitmqconnection.publishToExchange(this.name, if_unused);    
    }

}

export default Exchange;