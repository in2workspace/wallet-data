package es.in2.wallet.data.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wallet-crypto")
public record WalletCryptoProperties(String url) {
}

