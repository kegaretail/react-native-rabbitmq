package nl.kega.reactnativerabbitmq;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP;

public class RabbitMqExchange {

    public String name;
    public String type;
    public Boolean durable;
    public Boolean autodelete;
    public Boolean internal;

    private ReactApplicationContext context;

    private Channel channel;

    public RabbitMqExchange (ReactApplicationContext context, Channel channel, ReadableMap condig){
        
        this.channel = channel;

        this.name = condig.getString("name");
        this.type = (condig.hasKey("type") ? condig.getString("type") : "fanout");
        this.durable = (condig.hasKey("durable") ? condig.getBoolean("durable") : true);
        this.autodelete = (condig.hasKey("autoDelete") ? condig.getBoolean("autoDelete") : false);
        this.internal = (condig.hasKey("internal") ? condig.getBoolean("internal") : false);

        Map<String, Object> args = new HashMap<String, Object>();

        try {

            this.channel.exchangeDeclare(this.name, this.type, this.durable, this.autodelete, this.internal, args);

        } catch (Exception e){
            Log.e("RabbitMqExchange", "Exchange error " + e);
            e.printStackTrace();
        }

    }

    public void publish(String message, String routing_key){ 
        try {
            byte[] message_body_bytes = message.getBytes();

            AMQP.BasicProperties properties = new AMQP.BasicProperties();
            //properties.setExpiration("60000");
       
            this.channel.basicPublish(this.name, routing_key, properties, message_body_bytes);
        } catch (Exception e){
            Log.e("RabbitMqExchange", "Exchange publish error " + e);
            e.printStackTrace();
        }
    }

    public void delete(Boolean ifUnused){ 
        try {
            this.channel.exchangeDelete(this.name, ifUnused);
        } catch (Exception e){
            Log.e("RabbitMqExchange", "Exchange delete error " + e);
            e.printStackTrace();
        }
    }

}

       