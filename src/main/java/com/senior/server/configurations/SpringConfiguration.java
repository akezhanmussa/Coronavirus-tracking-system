package com.senior.server.configurations;

import com.authy.AuthyApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    @Autowired
    private TwilioAccountConfiguration twilioAccountConfiguration;

    @Bean
    public AuthyApiClient authyApiClient() {
        return new AuthyApiClient(twilioAccountConfiguration.getAuthId());
    }
}
