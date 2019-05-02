package nl.kega.reactnativerabbitmq;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;

public class RabbitMqExchange {

    public String name;
    public String type;
    public Boolean durable;
    public Boolean autodelete;
    public Boolean internal;

    private ReactApplicationContext context;

    private Channel channel;

    public RabbitMqExchange (ReactApplicationContext context, Channel channel, ReadableMap config){
        
        this.channel = channel;

        this.name = config.getString("name");
        this.type = (config.hasKey("type") ? config.getString("type") : "fanout");
        this.durable = (config.hasKey("durable") ? config.getBoolean("durable") : true);
        this.autodelete = (config.hasKey("autoDelete") ? config.getBoolean("autoDelete") : false);
        this.internal = (config.hasKey("internal") ? config.getBoolean("internal") : false);

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
                 try {
                     
                    if (message_properties.hasKey("content_type") && message_properties.getType("content_type") == ReadableType.String){
                        properties.contentType(message_properties.getString("content_type"));
                    }
                    if (message_properties.hasKey("content_encoding") && message_properties.getType("content_encoding") == ReadableType.String){
                        properties.contentEncoding(message_properties.getString("content_encoding"));
                    }
                    if (message_properties.hasKey("delivery_mode") && message_properties.getType("delivery_mode") == ReadableType.Number){
                        properties.deliveryMode(message_properties.getInt("delivery_mode"));
                    }
                    if (message_properties.hasKey("priority") && message_properties.getType("priority") == ReadableType.Number){
                        properties.priority(message_properties.getInt("priority"));
                    }
                    if (message_properties.hasKey("correlation_id") && message_properties.getType("correlation_id") == ReadableType.String){
                        properties.correlationId(message_properties.getString("correlation_id"));
                    }
                    if (message_properties.hasKey("expiration") && message_properties.getType("expiration") == ReadableType.String){ 
                        properties.expiration(message_properties.getString("expiration"));
                    }
                    if (message_properties.hasKey("message_id") && message_properties.getType("message_id") == ReadableType.String){
                        properties.messageId(message_properties.getString("message_id"));
                    }
                    if (message_properties.hasKey("type") && message_properties.getType("type") == ReadableType.String){
                        properties.type(message_properties.getString("type"));
                    }
                    if (message_properties.hasKey("user_id") && message_properties.getType("user_id") == ReadableType.String){
                        properties.userId(message_properties.getString("user_id"));
                    }
                    if (message_properties.hasKey("app_id") && message_properties.getType("app_id") == ReadableType.String){
                        properties.appId(message_properties.getString("app_id"));
                    }
                    if (message_properties.hasKey("reply_to") && message_properties.getType("reply_to") == ReadableType.String){
                        properties.replyTo(message_properties.getString("reply_to"));
                    }

                    //if (message_properties.hasKey("timestamp")){properties.timestamp(message_properties.getBoolean("timestamp"));}
                    //if (message_properties.hasKey("headers")){properties.expiration(message_properties.getBoolean("headers"))}

                 } catch (Exception e){
                    Log.e("RabbitMqExchange", "Exchange publish properties error " + e);
                    e.printStackTrace();
                }
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

       