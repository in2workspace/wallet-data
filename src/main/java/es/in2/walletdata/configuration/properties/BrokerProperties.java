package es.in2.walletdata.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * Broker Properties
 *
 * @param domain - Broker domain
 * @param url - Broker URL
 */
@ConfigurationProperties(prefix = "broker")
public record BrokerProperties(String domain, String url) {

    @ConstructorBinding
    public BrokerProperties(String domain, String url) {
        this.domain = Optional.ofNullable(domain).orElse("/ngsi-ld/v1/entities");
        this.url = Optional.ofNullable(url).orElse("http://localhost:9090");
    }

}
