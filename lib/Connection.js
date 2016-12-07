import {NativeModules, DeviceEventEmitter} from 'react-native';

const RabbitMqConnection = NativeModules.RabbitMqConnection;

export class Connection {
    
    constructor(config) {
        this.rabbitmqconnection = RabbitMqConnection;
        this.callbacks = {};

        DeviceEventEmitter.addListener('RabbitMqConnectionEvent', this.handleEvent.bind(this));

        this.rabbitmqconnection.initialize(config);

    }
    
    connect(){
        this.rabbitmqconnection.connect();
    }    
    
    close(){
        this.rabbitmqconnection.close();
    }

    handleEvent(event){

        if (this.callbacks.hasOwnProperty(event.name)){
            this.callbacks[event.name](event)
        }

    }
    
    on(event, callback){
        this.callbacks[event] = callback;
    } 

}

export default Connection;