package nl.kega.reactnativerabbitmq;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;

public class RabbitMqQueue {

    public String name;
    public String routing_key;
    public String consumer_tag;
    public Boolean passive;
    public Boolean exclusive;
    public Boolean durable;
    public Boolean autodelete;
    public Boolean autoack;
    public ReadableMap consumer_arguments;
       
    private ReactApplicationContext context;

    private Channel channel;
    private RabbitMqExchange exchange;

    public RabbitMqQueue (ReactApplicationContext context, Channel channel, ReadableMap queue_config, ReadableMap arguments){
       
        this.context = context;
        this.channel = channel;

        this.name = queue_config.getString("name");
        this.exclusive = (queue_config.hasKey("exclusive") ? queue_config.getBoolean("exclusive") : false);
        this.durable = (queue_config.hasKey("durable") ? queue_config.getBoolean("durable") : true);
        this.autodelete = (queue_config.hasKey("autoDelete") ? queue_config.getBoolean("autoDelete") : false);
        this.autoack = (queue_config.hasKey("autoAck") ? queue_config.getBoolean("autoAck") : false);

        this.consumer_arguments = (queue_config.hasKey("consumer_arguments") ? queue_config.getMap("consumer_arguments") : null);
     
        Map<String, Object> args = toHashMap(arguments);

        try {
            RabbitMqConsumer consumer = new RabbitMqConsumer(this.channel, this);

            Map<String, Object> consumer_args = toHashMap(this.consumer_arguments);

            this.channel.queueDeclare(this.name, this.durable, this.exclusive, this.autodelete, args);
            this.channel.basicConsume(this.name, this.autoack, consumer_args, consumer);

         

        } catch (Exception e){
            Log.e("RabbitMqQueue", "Queue error " + e);
            e.printStackTrace();
        }
    }

    public void onMessage(WritableMap message){
        Log.e("RabbitMqQueue", message.getString("message"));

        message.putString("queue_name", this.name);

        this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RabbitMqQueueEvent", message);
    }

    public void bind(RabbitMqExchange exchange, String routing_key){ 
        try {
            this.exchange = exchange;
            this.routing_key = (routing_key == "" ? this.name : routing_key);

            this.channel.queueBind(this.name, this.exchange.name, this.routing_key);
        } catch (Exception e){
            Log.e("RabbitMqQueue", "Queue bind error " + e);
            e.printStackTrace();
        }
    }

    public void unbind(String routing_key){ 
        try {
  
            if (!this.exchange.equals(null)){
                this.channel.queueUnbind(this.name, this.exchange.name, routing_key);
                this.exchange = null;
            }
            
        } catch (Exception e){
            Log.e("RabbitMqQueue", "Queue unbind error " + e);
            e.printStackTrace();
        }
    }
    /*
    public void publish(String message, String routing_key){ 
        try {
            byte[] message_body_bytes = message.getBytes();

            AMQP.BasicProperties properties = new AMQP.BasicProperties();
            //properties.setExpiration("60000");
       
            this.channel.basicPublish(this.exchange_name, routing_key, properties, message_body_bytes);
        } catch (Exception e){
            Log.e("RabbitMqQueue", "Queue publish error " + e);
            e.printStackTrace();
        }
    }
    */
    public void purge(){ 
        try {
            //this.channel.queuePurge(this.name, true); 
            
            WritableMap event = Arguments.createMap();
            event.putString("name", "purged");
            event.putString("queue_name", this.name);

            this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RabbitMqQueueEvent", event);
        } catch (Exception e){
            Log.e("RabbitMqQueue", "Queue purge error " + e);
            e.printStackTrace();
        }
    } 

    public void delete(){ 
        try {
            this.channel.queueDelete(this.name); 
            
            WritableMap event = Arguments.createMap();
            event.putString("name", "deleted");
            event.putString("queue_name", this.name);

            this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RabbitMqQueueEvent", event);
        } catch (Exception e){
            Log.e("RabbitMqQueue", "Queue delete error " + e);
            e.printStackTrace();
        }
    } 


    private Map<String, Object> toHashMap(ReadableMap data){

        Map<String, Object> args = new HashMap<String, Object>();

        if (data == null){
            return args;
        }

        ReadableMapKeySetIterator iterator = data.keySetIterator();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();

            ReadableType readableType = data.getType(key);

            switch (readableType) {
                case Null:
                    args.put(key, key);
                    break;
                case Boolean:
                    args.put(key, data.getBoolean(key));
                    break;
                case Number:
                    // Can be int or double.
                    double tmp = data.getDouble(key);
                    if (tmp == (int) tmp) {
                        args.put(key, (int) tmp);
                    } else {
                        args.put(key, tmp);
                    }
                    break;
                case String:
                    Log.e("RabbitMqQueue", data.getString(key));
                    args.put(key, data.getString(key));
                    break;

            }
        }


        return args;
    }

    public void basicAck(long delivery_tag) {
        try {
            this.channel.basicAck(delivery_tag, false);
        } catch (IOException e){
            Log.e("RabbitMqQueue", "basicAck " + e);
            e.printStackTrace();
        } catch (AlreadyClosedException e){
            Log.e("RabbitMqQueue AlreadyClosedException", "basicAck " + e);
            e.printStackTrace();
        } 
  
    }

}
