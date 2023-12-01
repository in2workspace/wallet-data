package es.in2.wallet.data.api.config;

import es.in2.wallet.data.api.config.properties.BrokerAdapterProperties;
import es.in2.wallet.data.api.config.properties.OpenApiProperties;
import es.in2.wallet.data.api.config.properties.WalletCryptoProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfigs {

    private final WalletCryptoProperties walletCryptoProperties;
    private final BrokerAdapterProperties brokerAdapterProperties;
    private final OpenApiProperties openApiProperties;

    @PostConstruct
    void init() {
        String prefixMessage = " > {}";
        log.info("Configurations uploaded: ");
        log.info(prefixMessage, openApiProperties.server());
        log.info(prefixMessage, openApiProperties.info());
        log.info(prefixMessage, walletCryptoProperties);
        log.info(prefixMessage, brokerAdapterProperties);
    }

}
