package es.in2.wallet.data.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.server.url}")
    private String serverUrl;

    @Value("${openapi.server.description}")
    private String serverDescription;

    @Value("${openapi.info.title}")
    private String infoTitle;

    @Value("${openapi.info.version}")
    private String infoVersion;

    @Value("${openapi.info.termsOfService}")
    private String infoTermsOfService;

    @Value("${openapi.info.license.name}")
    private String infoLicenseName;

    @Value("${openapi.info.license.url}")
    private String infoLicenseUrl;

    @Value("${openapi.info.contact.email}")
    private String infoContactEmail;

    @Value("${openapi.info.contact.name}")
    private String infoContactName;

    @Value("${openapi.info.contact.url}")
    private String infoContactUrl;

    @Value("${openapi.info.description}")
    private String infoDescription;

    @Bean
    public OpenAPI myOpenAPI() {
        // Defining servers
        Server devServer = getServer();
        // Defining contact info
        Contact contact = getContact();
        // Defining license info
        License mitLicense = getLicense();
        // Defining application info
        Info info = getInfo(contact, mitLicense);
        return new OpenAPI().info(info).servers(List.of(devServer));
    }

    private Server getServer() {
        Server devServer = new Server();
        devServer.setUrl(serverUrl);
        devServer.setDescription(serverDescription);
        return devServer;
    }

    private Info getInfo(Contact contact, License mitLicense) {
        Info info = new Info();
        info.setTitle(infoTitle);
        info.setVersion(infoVersion);
        info.setContact(contact);
        info.setDescription(infoDescription);
        info.setTermsOfService(infoTermsOfService);
        info.setLicense(mitLicense);
        return info;
    }

    private License getLicense() {
        License mitLicense = new License();
        mitLicense.setName(infoLicenseName);
        mitLicense.setUrl(infoLicenseUrl);
        return mitLicense;
    }

    private Contact getContact() {
        Contact contact = new Contact();
        contact.email(infoContactEmail);
        contact.name(infoContactName);
        contact.url(infoContactUrl);
        return contact;
    }
}
