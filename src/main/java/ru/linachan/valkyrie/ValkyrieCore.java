package ru.linachan.valkyrie;

import com.rabbitmq.client.ConnectionFactory;
import ru.linachan.yggdrasil.component.YggdrasilComponent;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class ValkyrieCore extends YggdrasilComponent {

    private ConnectionFactory rabbitConnectionFactory = null;

    @Override
    protected void onInit() {
        String rabbitHost = core.getConfig("ValkyrieRabbitMQHost", "127.0.0.1");
        Integer rabbitPort = Integer.parseInt(core.getConfig("ValkyrieRabbitMQPort", "5572"));

        String rabbitVirtualHost = core.getConfig("ValkyrieRabbitMQVirtualHost", "/");

        String rabbitUser = core.getConfig("ValkyrieRabbitMQUser", "valkyrie");
        String rabbitPassword = core.getConfig("ValkyrieRabbitMQPassword", "valkyriePassword");

        rabbitConnectionFactory = new ConnectionFactory();

        rabbitConnectionFactory.setUsername(rabbitUser);
        rabbitConnectionFactory.setPassword(rabbitPassword);
        rabbitConnectionFactory.setVirtualHost(rabbitVirtualHost);
        rabbitConnectionFactory.setHost(rabbitHost);
        rabbitConnectionFactory.setPort(rabbitPort);
    }

    @Override
    protected void onShutdown() {

    }

    @Override
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

    public ValkyrieMessaging getMessenger() {
        try {
            return new ValkyrieMessaging(core, rabbitConnectionFactory.newConnection());
        } catch (TimeoutException | IOException e) {
            core.logException(e);
            return null;
        }
    }
}
