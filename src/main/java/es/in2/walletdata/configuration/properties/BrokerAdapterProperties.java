package es.in2.walletdata.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * Broker Adapter Properties
 *
 * @param url - Broker Adapter URL
 */
@ConfigurationProperties(prefix = "broker-adapter")
public record BrokerAdapterProperties(String url) {

    @ConstructorBinding
    public BrokerAdapterProperties(String url) {
        this.url = Optional.ofNullable(url).orElse("http://localhost:8090");
    }

}
