package com.senior.server.configurations;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "account")
public class TwilioAccountConfiguration {

    private String authToken;
    private String accountSid;
    private String verificationSid;
    private String authId;

    public String getVerificationSid() {
        return verificationSid;
    }

    public void setVerificationSid(String verificationSid) {
        this.verificationSid = verificationSid;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }
}
