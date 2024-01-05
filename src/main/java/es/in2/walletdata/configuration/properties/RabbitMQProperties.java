package es.in2.walletdata.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * RabbitMQ Configuration Properties
 *
 * @param host - rabbitmq host
 * @param port - rabbitmq port
 * @param username - rabbitmq username
 * @param password - rabbitmq password
 * @param virtualHost - rabbitmq virtual host
 */
@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitMQProperties(String host, Integer port, String username, String password, String virtualHost
) {

    @ConstructorBinding
    public RabbitMQProperties(String host, Integer port, String username, String password, String virtualHost) {
        this.host = Optional.ofNullable(host).orElse("rabbitmq");
        this.port = Optional.ofNullable(port).orElse(5672);
        this.username = Optional.ofNullable(username).orElse("guest");
        this.password = Optional.ofNullable(password).orElse("guest");
        this.virtualHost = Optional.ofNullable(virtualHost).orElse("vhost");
    }

}
