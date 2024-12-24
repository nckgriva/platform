package com.gracelogic.platform.payment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class PaymentMessageSourceConfiguration {
    @Bean
    public ResourceBundleMessageSource paymentMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.payment");
        return messageSource;
    }
}
