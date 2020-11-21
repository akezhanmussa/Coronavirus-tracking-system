package com.senior.server.controllers;


import com.senior.server.domain.User;
import com.senior.server.repositories.UserRepository;
import com.senior.server.services.PhoneNumberVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(path = "/api")
public class UserVerificationController {

    private static final Logger logger = LoggerFactory.getLogger(UserVerificationController.class);
    private PhoneNumberVerificationService phoneNumberVerificationService;
    private UserRepository userRepository;

    @Autowired
    public UserVerificationController(PhoneNumberVerificationService phoneNumberVerificationService, UserRepository userRepository) {
        this.phoneNumberVerificationService = phoneNumberVerificationService;
        this.userRepository = userRepository;
    }
    
    @RequestMapping(path = "database-check", method = RequestMethod.POST)
    public ResponseEntity<?> checkIdInDatabase(@RequestBody Map<String, String> requestBody){
        String id = requestBody.getOrDefault("id", "-1");
        User candidateUser = userRepository.getUserWithId(id);
        if (candidateUser == null){
            logger.info("User was not found");
            Map<String, String> errorMessage = new HashMap();
            errorMessage.put("errorMessage", "No such user with this id exists");
            return new ResponseEntity<Map<String, String>>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        logger.info("User was found");
        return new ResponseEntity<User>(candidateUser, HttpStatus.OK);
    }

//    @RequestMapping(path = "start", method = RequestMethod.POST)
//    public ResponseEntity<?> start(@RequestBody Map<String, String> requestBody) {
//        String phoneNumber = requestBody.getOrDefault("phoneNumber", "+77015137789");
//        String via = requestBody.getOrDefault("via", Verification.Channel.SMS.toString());
//        Verification verification = phoneNumberVerificationService.start(phoneNumber, via);
//        Map<String, String> rawRes = new HashMap();
//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        if (verification.getValid()){
//            rawRes.put("ans", "success");
//            status = HttpStatus.OK;
//        }else{
//            rawRes.put("ans", "failure");
//        }
//        return new ResponseEntity<Map<String, String>>(rawRes, status);
//    }

}
