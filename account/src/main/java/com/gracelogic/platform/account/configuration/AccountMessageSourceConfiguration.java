package com.gracelogic.platform.account.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class AccountMessageSourceConfiguration {
    @Bean
    public ResourceBundleMessageSource accountMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.account");
        return messageSource;
    }
}
