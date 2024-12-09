package com.gracelogic.platform.user.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class UserMessageSourceConfiguration {
    @Bean
    public ResourceBundleMessageSource userMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.user");
        return messageSource;
    }
}
