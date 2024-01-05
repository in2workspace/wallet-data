package es.in2.walletdata.configuration;

import es.in2.walletdata.configuration.properties.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfigs {

    private final WalletCryptoProperties walletCryptoProperties;
    private final BrokerProperties brokerProperties;
    private final OpenApiProperties openApiProperties;
    private final WalletDrivingApplicationProperties walletDrivingApplicationProperties;
    private final RabbitMQProperties rabbitMQProperties;

    @PostConstruct
    void init() {
        String prefixMessage = " > {}";
        log.debug("Configurations uploaded: ");
        log.debug("OpenAPI properties: ");
        log.debug(prefixMessage, openApiProperties.server());
        log.debug(prefixMessage, openApiProperties.info());
        log.debug("Wallet Crypto properties: ");
        log.debug(prefixMessage, walletCryptoProperties);
        log.debug("Broker properties: ");
        log.debug(prefixMessage, brokerProperties);
        log.debug("Wallet Driving Application properties: ");
        log.debug(prefixMessage, walletDrivingApplicationProperties);
        log.debug("RabbitMQ properties: ");
        log.debug(prefixMessage, rabbitMQProperties);
    }

}
