package com.senior.server.controllers;


import com.authy.AuthyApiClient;
import com.senior.server.services.PhoneNumberVerificationService;
import com.twilio.rest.verify.v2.service.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/phone-number-verify")
public class PhoneNumberVerificationController {

//    private AuthyApiClient authyApiClient;

    private PhoneNumberVerificationService phoneNumberVerificationService;

    @Autowired
    public PhoneNumberVerificationController(PhoneNumberVerificationService phoneNumberVerificationService) {
        this.phoneNumberVerificationService = phoneNumberVerificationService;
    }

    @RequestMapping(path = "start", method = RequestMethod.POST)
    public ResponseEntity<?> start(@RequestBody Map<String, String> requestBody) {
//        authyApiClient.getPhoneVerification();
        String phoneNumber = requestBody.getOrDefault("phoneNumber", "+77015137789");
        String via = requestBody.getOrDefault("via", Verification.Channel.SMS.toString());
        Verification verification = phoneNumberVerificationService.start(phoneNumber, via);
        Map<String, String> rawRes = new HashMap();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (verification.getValid()){
            rawRes.put("ans", "success");
            status = HttpStatus.OK;
        }else{
            rawRes.put("ans", "failure");
        }
        return new ResponseEntity<Map<String, String>>(rawRes, status);
    }

}
