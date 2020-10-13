package com.senior.server.services;

import com.twilio.rest.verify.v2.service.Verification;

public interface PhoneNumberVerificationService {

    Verification start(String phoneNumber, String via);
    void verify(String phoneNumber, String token);

}
