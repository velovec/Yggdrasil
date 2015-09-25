package ru.linachan.valkyrie;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import ru.linachan.yggdrasil.YggdrasilCore;

public interface ValkyrieRoute {

    void handleMessage(YggdrasilCore core, String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body);
}
