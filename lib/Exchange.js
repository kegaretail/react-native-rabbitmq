import { DeviceEventEmitter } from 'react-native';

export class Exchange {

    constructor(connection, exchange_config) {

        this.callbacks = {};
        this.rabbitmqconnection = connection.rabbitmqconnection;

        this.name = exchange_config.name;
        this.exchange_config = exchange_config;

        this.rabbitmqconnection.addExchange(exchange_config);
    }

    on(event, callback){
        this.callbacks[event] = callback;
    }

    removeon(event){
        delete this.callbacks[event];
    }

    publish(message, routing_key = '', properties = {}){
        this.rabbitmqconnection.publishToExchange(message, this.name, routing_key, properties);
    }

    delete(){
        this.rabbitmqconnection.deleteExchange(this.name);
    }

}

export default Exchange;
