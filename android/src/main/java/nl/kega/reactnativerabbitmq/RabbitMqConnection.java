package nl.kega.reactnativerabbitmq;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.GuardedAsyncTask;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ShutdownListener; 
import com.rabbitmq.client.ShutdownSignalException; 
import com.rabbitmq.client.RecoveryListener;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoverableConnection;
import com.rabbitmq.client.ConfirmListener;

class RabbitMqConnection extends ReactContextBaseJavaModule  {

    private ReactApplicationContext context;

    public ReadableMap config;

    private ConnectionFactory factory = null;
    private RecoverableConnection connection;
    private Channel channel;

    private Callback status;

    private ArrayList<RabbitMqQueue> queues = new ArrayList<RabbitMqQueue>();
    private ArrayList<RabbitMqExchange> exchanges = new ArrayList<RabbitMqExchange>(); 

    public RabbitMqConnection(ReactApplicationContext reactContext) {
        super(reactContext);

        this.context = reactContext;

    }

    @Override
    public String getName() {
        return "RabbitMqConnection";
    }

    @ReactMethod
    public void initialize(ReadableMap config) {
        this.config = config;
        
        this.factory = new ConnectionFactory();
        this.factory.setUsername(this.config.getString("username"));
        this.factory.setPassword(this.config.getString("password"));
        this.factory.setVirtualHost(this.config.getString("virtualhost"));
        this.factory.setHost(this.config.getString("host"));
        this.factory.setPort(this.config.getInt("port"));
        this.factory.setAutomaticRecoveryEnabled(true);
        this.factory.setRequestedHeartbeat(10);
   
        try {
            if (this.config.hasKey("ssl") && this.config.getBoolean("ssl")) {
                this.factory.useSslProtocol();
            }
        } catch(Exception e) {
            Log.e("RabbitMqConnection", e.toString());
        }

    }

    @ReactMethod
    public void status(Callback onStatus) {
        this.status = onStatus;
    }

    @ReactMethod
    public void connect() {

        if (this.connection != null && this.connection.isOpen()){
            WritableMap event = Arguments.createMap();
            event.putString("name", "connected");

            this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RabbitMqConnectionEvent", event);
        }else{ 

            try {
                this.connection = (RecoverableConnection)this.factory.newConnection();
            } catch (Exception e){

                WritableMap event = Arguments.createMap();
                event.putString("name", "error");
                event.putString("type", "failedtoconnect");
                event.putString("code", "");
                event.putString("description", e.getMessage());

                this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RabbitMqConnectionEvent", event);

                this.connection = null; 

            }

            if (this.connection != null){

                try {

                    this.connection.addShutdownListener(new ShutdownListener() {
                        @Override
                        public void shutdownCompleted(ShutdownSignalException cause) {
                            Log.e("RabbitMqConnection", "Shutdown signal received " + cause);
                            onClose(cause);
                        }
                    });

                 
                    this.connection.addRecoveryListener(new RecoveryListener() {
                      
                        @Override
                        public void handleRecoveryStarted(Recoverable recoverable) {
                            Log.e("RabbitMqConnection", "RecoveryStarted " + recoverable);
                        }
                      
                        @Override
                        public void handleRecovery(Recoverable recoverable) {
                            Log.e("RabbitMqConnection", "Recoverable " + recoverable);
                            onRecovered();
                        }
                        
                    });
                   

                    this.channel = connection.createChannel();
                    this.channel.basicQos(1);

                    this.channel.addConfirmListener(new ConfirmListener() {

                        public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                            Log.e("RabbitMqQueue", "Not ack received");
                        }
                
                        public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                            Log.e("RabbitMqQueue", "Ack received");
                        }
                    });


                    WritableMap event = Arguments.createMap();
                    event.putString("name", "connected");

                    this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RabbitMqConnectionEvent", event);

                } catch (Exception e){

                    Log.e("RabbitMqConnectionChannel", "Create channel error " + e);
                    e.printStackTrace();

                } 
            }
        }
    }

    @ReactMethod
    public void addQueue(ReadableMap queue_config, ReadableMap arguments) {
        RabbitMqQueue queue = new RabbitMqQueue(this.context, this.channel, queue_config, arguments);
        this.queues.add(queue);
    }

    @ReactMethod
    public void bindQueue(String exchange_name, String queue_name, String routing_key) {
        
        RabbitMqQueue found_queue = null;
        for (RabbitMqQueue queue : queues) {
		    if (Objects.equals(queue_name, queue.name)){
                found_queue = queue;
            }
        }

        RabbitMqExchange found_exchange = null;
        for (RabbitMqExchange exchange : exchanges) {
		    if (Objects.equals(exchange_name, exchange.name)){
                found_exchange = exchange;
            }
        }

        if (!found_queue.equals(null) && !found_exchange.equals(null)){
            found_queue.bind(found_exchange, routing_key);
        }
    }

    @ReactMethod
    public void unbindQueue(String exchange_name, String queue_name, String routing_key) {
        
        RabbitMqQueue found_queue = null;
        for (RabbitMqQueue queue : queues) {
		    if (Objects.equals(queue_name, queue.name)){
                found_queue = queue;
            }
        }

        RabbitMqExchange found_exchange = null;
        for (RabbitMqExchange exchange : exchanges) {
		    if (Objects.equals(exchange_name, exchange.name)){
                found_exchange = exchange;
            }
        }

        if (!found_queue.equals(null) && !found_exchange.equals(null)){
            found_queue.unbind(routing_key);
        }
    }

    @ReactMethod
    public void removeQueue(String queue_name) {
        RabbitMqQueue found_queue = null;
        for (RabbitMqQueue queue : queues) {
		    if (Objects.equals(queue_name, queue.name)){
                found_queue = queue;
            }
        }

        if (!found_queue.equals(null)){
            found_queue.delete();
        }
    }

    @ReactMethod
    public void basicAck(String queue_name, Double delivery_tag) {
        RabbitMqQueue found_queue = null;
        for (RabbitMqQueue queue : queues) {
		    if (Objects.equals(queue_name, queue.name)){
                found_queue = queue;
            }
        }

        if (!found_queue.equals(null)){
            long long_delivery_tag = Double.valueOf(delivery_tag).longValue();
            found_queue.basicAck(long_delivery_tag);
        }

     
    }

    /*
    @ReactMethod
    public void publishToQueue(String message, String exchange_name, String routing_key) {

        for (RabbitMqQueue queue : queues) {
		    if (Objects.equals(exchange_name, queue.exchange_name)){
                Log.e("RabbitMqConnection", "publish " + message);
                queue.publish(message, exchange_name);
                return;
            }
		}

    }
    */

    @ReactMethod
    public void addExchange(ReadableMap exchange_config) {

        RabbitMqExchange exchange = new RabbitMqExchange(this.context, this.channel, exchange_config);

        this.exchanges.add(exchange);
    }

    @ReactMethod
    public void publishToExchange(String message, String exchange_name, String routing_key, ReadableMap message_properties) {

         for (RabbitMqExchange exchange : exchanges) {
		    if (Objects.equals(exchange_name, exchange.name)){
                Log.e("RabbitMqConnection", "Exchange publish: " + message);
                exchange.publish(message, routing_key, message_properties);
                return;
            }
		}

    }

    @ReactMethod
    public void deleteExchange(String exchange_name, Boolean if_unused) {

        for (RabbitMqExchange exchange : exchanges) {
		    if (Objects.equals(exchange_name, exchange.name)){
                exchange.delete(if_unused);
                return;
            }
		}

    }

    @ReactMethod
    public void close() {
        try {

            this.queues = new ArrayList<RabbitMqQueue>();
            this.exchanges = new ArrayList<RabbitMqExchange>(); 
            
            this.channel.close();

            this.connection.close();
         } catch (Exception e){
            Log.e("RabbitMqConnection", "Connection closing error " + e);
            e.printStackTrace();
        } finally { 
            this.connection = null; 
            this.factory = null;
            this.channel = null;
        } 
    }

    private void onClose(ShutdownSignalException cause) { 
        Log.e("RabbitMqConnection", "Closed");

        WritableMap event = Arguments.createMap();
        event.putString("name", "closed");

        this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RabbitMqConnectionEvent", event);
    } 

    private void onRecovered() { 
        Log.e("RabbitMqConnection", "Recovered");

        WritableMap event = Arguments.createMap();
        event.putString("name", "reconnected");

        this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RabbitMqConnectionEvent", event);
    } 

    @Override
    public void onCatalystInstanceDestroy() {

        // serialize on the AsyncTask thread, and block
        try {
            new GuardedAsyncTask<Void, Void>(getReactApplicationContext()) {
                @Override
                protected void doInBackgroundGuarded(Void... params) {
                    close();
                }
            }.execute().get();
        } catch (InterruptedException ioe) {
            Log.e("RabbitMqConnection", "onCatalystInstanceDestroy", ioe);
        } catch (ExecutionException ee) {
            Log.e("RabbitMqConnection", "onCatalystInstanceDestroy", ee);
        }
    }

}
