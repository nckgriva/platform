package com.gracelogic.platform.db.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class DBMessageSourceConfiguration {

    @Bean
    public ResourceBundleMessageSource dbMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.db");
        return messageSource;
    }

    @Bean
    public ResourceBundleMessageSource coreMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.core");
        return messageSource;
    }
}
