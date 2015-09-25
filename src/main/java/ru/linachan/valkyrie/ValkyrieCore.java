package ru.linachan.valkyrie;

import com.rabbitmq.client.ConnectionFactory;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class ValkyrieCore {

    private YggdrasilCore core;

    private ConnectionFactory rabbitConnectionFactory = null;

    public ValkyrieCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;

        this.core.logInfo("Initializing Valkyrie Interaction System...");

        String rabbitHost = this.core.getConfig("ValkyrieRabbitMQHost", "127.0.0.1");
        Integer rabbitPort = Integer.parseInt(this.core.getConfig("ValkyrieRabbitMQPort", "5572"));

        String rabbitVirtualHost = this.core.getConfig("ValkyrieRabbitMQVirtualHost", "/");

        String rabbitUser = this.core.getConfig("ValkyrieRabbitMQUser", "valkyrie");
        String rabbitPassword = this.core.getConfig("ValkyrieRabbitMQPassword", "valkyriePassword");

        this.rabbitConnectionFactory = new ConnectionFactory();

        this.rabbitConnectionFactory.setUsername(rabbitUser);
        this.rabbitConnectionFactory.setPassword(rabbitPassword);
        this.rabbitConnectionFactory.setVirtualHost(rabbitVirtualHost);
        this.rabbitConnectionFactory.setHost(rabbitHost);
        this.rabbitConnectionFactory.setPort(rabbitPort);

    }

    public ValkyrieMessaging getMessenger() {
        try {
            return new ValkyrieMessaging(this.core, this.rabbitConnectionFactory.newConnection());
        } catch (TimeoutException | IOException e) {
            this.core.logException(e);
            return null;
        }
    }

    public boolean executeTests() {
        try {
            ValkyrieMessaging msg = getMessenger();

            if (msg != null) {
                msg.declareQueue("test_queue");
                msg.declareExchange("test_exchange");
                msg.bindQueue("test_queue", "test_exchange", "test_route");

                byte[] test_message = "ValkyrieTestMessage".getBytes();

                msg.publishMessage("test_exchange", "test_route", true, test_message);
                Boolean res = (Arrays.equals(msg.getResponse("test_queue", true).getBody(), test_message));

                msg.closeConnections();

                return res;
            }
        } catch (Exception e) {
            core.logException(e);
        }
        return false;
    }
}
