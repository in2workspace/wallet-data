package es.in2.walletdata.configuration;

import es.in2.walletdata.configuration.properties.BrokerProperties;
import es.in2.walletdata.configuration.properties.OpenApiProperties;
import es.in2.walletdata.configuration.properties.WalletCryptoProperties;
import es.in2.walletdata.configuration.properties.WalletDrivingApplicationProperties;
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

    @PostConstruct
    void init() {
        String prefixMessage = " > {}";
        log.info("Configurations uploaded: ");
        log.info(prefixMessage, openApiProperties.server());
        log.info(prefixMessage, openApiProperties.info());
        log.info(prefixMessage, walletCryptoProperties);
        log.info(prefixMessage, brokerProperties);
        log.info(prefixMessage, walletDrivingApplicationProperties);
    }

}
