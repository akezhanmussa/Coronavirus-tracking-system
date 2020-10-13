package com.senior.server.services;

import com.senior.server.configurations.TwilioAccountConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCreator;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.rest.verify.v2.service.VerificationCheckCreator;

@Service
public class PhoneNumberVerificationServiceImpl implements PhoneNumberVerificationService
{

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberVerificationServiceImpl.class);
    private TwilioAccountConfiguration twilioAccountConfiguration;

    @Autowired
    public PhoneNumberVerificationServiceImpl(TwilioAccountConfiguration twilioAccountConfiguration) {
        this.twilioAccountConfiguration = twilioAccountConfiguration;
        Twilio.init(this.twilioAccountConfiguration.getAccountSid(), this.twilioAccountConfiguration.getAuthToken());
    }

    @Override
    public Verification start(String phoneNumber, String via) {
        VerificationCreator verificationCenter = Verification.creator(
                twilioAccountConfiguration.getVerificationSid(),
                phoneNumber,
                via);
        Verification verification = verificationCenter.create();
        return verification;
    }

    @Override
    public void verify(String phoneNumber, String token) {

    }
}
