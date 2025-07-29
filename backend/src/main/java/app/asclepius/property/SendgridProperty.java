package app.asclepius.property;

import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@EnableAutoConfiguration
@EnableConfigurationProperties
@Component
@Configuration
@ConfigurationProperties(prefix = "sendgrid")
@Data
public class SendgridProperty {
    private String url;
    private String apiKey;
}
