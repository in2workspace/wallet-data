package es.in2.walletdata.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * Wallet Driving Application Properties
 *
 * @param domain
 */
@ConfigurationProperties(prefix = "wallet-wda")
public record WalletDrivingApplicationProperties(String domain) {

    @ConstructorBinding
    public WalletDrivingApplicationProperties(String domain) {
        this.domain = Optional.ofNullable(domain).orElse("http://localhost:8088");
    }

}
