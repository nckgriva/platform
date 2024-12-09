package com.gracelogic.platform.content.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class ContentMessageSourceConfiguration {
    @Bean
    public ResourceBundleMessageSource contentMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.content");
        return messageSource;
    }
}
