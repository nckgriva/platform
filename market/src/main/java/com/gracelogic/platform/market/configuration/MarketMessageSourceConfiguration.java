package com.gracelogic.platform.market.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MarketMessageSourceConfiguration {
    @Bean
    public ResourceBundleMessageSource marketMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.market");
        return messageSource;
    }
}
