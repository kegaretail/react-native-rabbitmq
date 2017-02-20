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

    public void publish(String message, String routing_key, ReadableMap message_properties){ 
        try {
            byte[] message_body_bytes = message.getBytes();

            AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();

            if (message_properties != null){
                if (message_properties.hasKey("content_type")){properties.contentType(message_properties.getString("content_type"));}
                if (message_properties.hasKey("content_encoding")){properties.contentEncoding(message_properties.getString("content_encoding"));}
                if (message_properties.hasKey("delivery_mode")){properties.deliveryMode(message_properties.getInt("delivery_mode"));}
                if (message_properties.hasKey("priority")){properties.priority(message_properties.getInt("priority"));}
                if (message_properties.hasKey("correlation_id")){properties.correlationId(message_properties.getString("correlation_id"));}
                if (message_properties.hasKey("expiration")){ properties.expiration(message_properties.getString("expiration"));}
                if (message_properties.hasKey("message_id")){properties.messageId(message_properties.getString("message_id"));}
                if (message_properties.hasKey("type")){properties.type(message_properties.getString("type"));}
                if (message_properties.hasKey("user_id")){properties.userId(message_properties.getString("user_id"));}
                if (message_properties.hasKey("app_id")){properties.appId(message_properties.getString("app_id"));}
                if (message_properties.hasKey("reply_to")){properties.replyTo(message_properties.getString("reply_to"));}

                //if (message_properties.hasKey("timestamp")){properties.timestamp(message_properties.getBoolean("timestamp"));}
                //if (message_properties.hasKey("headers")){properties.expiration(message_properties.getBoolean("headers"))}
            }
            
            this.channel.basicPublish(this.name, routing_key, properties.build(), message_body_bytes);
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

       