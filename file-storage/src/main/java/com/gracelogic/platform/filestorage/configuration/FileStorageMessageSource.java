package com.gracelogic.platform.filestorage.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class FileStorageMessageSource {
    @Bean
    public ResourceBundleMessageSource filestorageMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.filestorage");
        return messageSource;
    }
}
