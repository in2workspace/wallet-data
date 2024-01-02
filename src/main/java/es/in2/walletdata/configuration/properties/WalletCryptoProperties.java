package es.in2.walletdata.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * Wallet Crypto Properties
 *
 * @param domain - Wallet Crypto domain
 * @param url - Wallet Crypto URL
 */
@ConfigurationProperties(prefix = "wallet-crypto")
public record WalletCryptoProperties(String domain,String url) {

    @ConstructorBinding
    public WalletCryptoProperties(String domain, String url) {
        this.domain = Optional.ofNullable(domain).orElse("/api/v2/secrets");
        this.url = Optional.ofNullable(url).orElse("http://localhost:8081");
    }

}

