package ru.linachan.valkyrie;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionParameters;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;

public class ValkyrieCore {

    private YggdrasilCore core;

    private ConnectionFactory rabbitConnectionFactory = null;

    public ValkyrieCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;

        this.core.logInfo("Initializing Valkyrie Interaction System...");

        String rabbitUser = this.core.getConfig("ValkyrieRabbitMQUser", "valkyrie");
        String rabbitPassword = this.core.getConfig("ValkyrieRabbitMQPassword", "valkyriePassword");
        String rabbitVirtualHost = this.core.getConfig("ValkyrieRabbitMQVirtualHost", "/");

        ConnectionParameters rabbitConnectionParameters = new ConnectionParameters();

        rabbitConnectionParameters.setUsername(rabbitUser);
        rabbitConnectionParameters.setPassword(rabbitPassword);
        rabbitConnectionParameters.setVirtualHost(rabbitVirtualHost);

        this.rabbitConnectionFactory = new ConnectionFactory(rabbitConnectionParameters);
    }

    public Connection getRabbitMQConnection() throws IOException {
        String rabbitHost = this.core.getConfig("ValkyrieRabbitMQHost", "127.0.0.1");
        String rabbitPort = this.core.getConfig("ValkyrieRabbitMQPort", "5572");

        return this.rabbitConnectionFactory.newConnection(rabbitHost, Integer.parseInt(rabbitPort));
    }

    public Channel getRabbitMQChannel(Connection connection) throws IOException {
        return connection.createChannel();
    }

    public boolean executeTests() {
        return true;
    }
}
