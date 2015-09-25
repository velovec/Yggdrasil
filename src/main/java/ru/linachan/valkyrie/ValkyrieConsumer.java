package ru.linachan.valkyrie;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;
import java.util.HashMap;

public class ValkyrieConsumer extends DefaultConsumer {

    private YggdrasilCore core;
    private Channel channel;

    private HashMap<String, ValkyrieRoute> routeMapping = new HashMap<>();

    public ValkyrieConsumer(YggdrasilCore core, Channel channel) {
        super(channel);
        this.core = core;
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String routingKey = envelope.getRoutingKey();
        String contentType = properties.getContentType();
        long deliveryTag = envelope.getDeliveryTag();

        if (routeMapping.containsKey(routingKey)) {
            routeMapping.get(routingKey).handleMessage(core, consumerTag, envelope, properties, body);
        } else {
            core.logWarning("Unhandled delivery: " + routingKey + "[" + contentType + "]");
        }

        channel.basicAck(deliveryTag, false);
    }

    public boolean registerRoute(String routingKey, ValkyrieRoute route) {
        if (!routeMapping.containsKey(routingKey)) {
            routeMapping.put(routingKey, route);
            return true;
        }
        return false;
    }
}
