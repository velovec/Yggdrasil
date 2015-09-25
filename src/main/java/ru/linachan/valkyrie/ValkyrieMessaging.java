package ru.linachan.valkyrie;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ValkyrieMessaging {

    private YggdrasilCore core;
    private Connection rabbitConnection;
    private Channel rabbitChannel;

    public ValkyrieMessaging(YggdrasilCore yggdrasilCore, Connection rabbitMQConnection) throws IOException {
        this.core = yggdrasilCore;
        this.rabbitConnection = rabbitMQConnection;
        this.rabbitChannel = rabbitMQConnection.createChannel();
    }

    public void declareQueue(String queueName) throws IOException {
        rabbitChannel.queueDeclare(queueName, true, false, false, null);
    }

    public void declareExchange(String exchangeName) throws IOException {
        rabbitChannel.exchangeDeclare(exchangeName, "direct", true);
    }

    public void bindQueue(String queueName, String exchangeName, String routingKey) throws IOException {
        rabbitChannel.queueBind(queueName, exchangeName, routingKey);
    }

    public void publishMessage(String exchangeName, String routingKey, Boolean persistent, byte[] body) throws IOException {
        if (persistent) {
            rabbitChannel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_BASIC, body);
        } else {
            rabbitChannel.basicPublish(exchangeName, routingKey, MessageProperties.BASIC, body);
        }
    }

    public ValkyrieConsumer getConsumer() {
        return new ValkyrieConsumer(core, rabbitChannel);
    }

    public void registerConsumer(String queueName, Boolean autoACK, String tag, ValkyrieConsumer consumer) throws IOException {
        rabbitChannel.basicConsume(queueName, autoACK, tag, consumer);
    }

    public GetResponse getResponse(String queueName, Boolean autoACK) throws IOException {
        return rabbitChannel.basicGet(queueName, autoACK);
    }

    public void closeConnections() {
        try {
            this.rabbitChannel.close();
            this.rabbitConnection.close();
        } catch (IOException | TimeoutException e) {
            core.logException(e);
        }
    }
}
