package com.healthcare.service;

import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.entity.UserInfo;
import com.healthcare.model.RequestModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    ResponseEntity<APIResponseEntity<?>> addNewUser(UserInfo model);

    ResponseEntity<APIResponseEntity<?>> login(RequestModel model);
}
