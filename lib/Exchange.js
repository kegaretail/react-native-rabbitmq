import {NativeModules, DeviceEventEmitter} from 'react-native';

const RabbitMqConnection = NativeModules.RabbitMqConnection;

export class Exchange {

    constructor(connection, exchange_condig) {

        this.callbacks = {};
        this.rabbitmqconnection = connection.rabbitmqconnection;

        this.name = exchange_condig.name;
        this.exchange_condig = exchange_condig;

        DeviceEventEmitter.addListener('RabbitMqExchangeEvent', this.handleEvent.bind(this));

        this.rabbitmqconnection.addExchange(exchange_condig);
    }

    handleEvent(event){
        if (event.queue_name == this.name && this.callbacks.hasOwnProperty(event.name)){
            this.callbacks[event.name](event)
        }
    }

    on(event, callback){
        this.callbacks[event] = callback;
    } 
    
    removeon(event){
        delete this.callbacks[event];
    } 

    publish(message, routing_key = ''){

        console.log('[Exchange] Send: ' + message + ' To: ' + this.name);

        this.rabbitmqconnection.publishToExchange(message, this.name, routing_key);    
    }

    delete(){
        this.rabbitmqconnection.deleteExchange(this.name);    
    }

}

export default Exchange;