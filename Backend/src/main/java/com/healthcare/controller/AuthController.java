package com.healthcare.controller;

import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.entity.UserInfo;
import com.healthcare.model.RequestModel;
import com.healthcare.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    /**
     * method to add new user
     * @author YOGARAJ
     * @return
     */
    @PostMapping("/create/user")
    public ResponseEntity<APIResponseEntity<?>> addNewUser(@RequestBody UserInfo userInfo) {
        return service.addNewUser(userInfo);
    }

    /**
     * method to add new user
     * @author YOGARAJ
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<APIResponseEntity<?>> login(@RequestBody RequestModel model) {
        return service.login(model);
    }
}
