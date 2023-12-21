package com.denver7074.bot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

import java.net.URI;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VaultConfig {

    ObjectMapper objectMapper;
    VaultProperties vaultProperties;

    @Bean
    @SneakyThrows
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = VaultEndpoint.from(new URI(Objects.requireNonNull(vaultProperties.getUri())));
        return new VaultTemplate(endpoint, new TokenAuthentication(Objects.requireNonNull(vaultProperties.getToken())));
    }

    @Bean
    public BotConfig botConfig(VaultTemplate vaultTemplate, @Value("${spring.cloud.vault.path}") String botConfigPath) {
        return objectMapper.convertValue(vaultTemplate.read(botConfigPath).getRequiredData().get("data"), BotConfig.class);
    }

}
