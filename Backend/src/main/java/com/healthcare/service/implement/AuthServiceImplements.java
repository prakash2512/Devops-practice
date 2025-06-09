package com.healthcare.service.implement;
import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.entity.UserInfo;
import com.healthcare.model.RequestModel;
import com.healthcare.service.AuthService;
import com.healthcare.utils.JwtUtil;
import com.healthcare.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthServiceImplements implements AuthService, UserDetailsService {


    @Autowired
    private  MongoTemplate mongoTemplate;
    @Autowired
    @Lazy
    private  PasswordEncoder passwordEncoder;
    @Autowired
    @Lazy
    private  AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;




    @Override
    public ResponseEntity<APIResponseEntity<?>> addNewUser(UserInfo userInfo) {
        try {
            ResponseEntity<APIResponseEntity<?>> ok = getUserInfoResponse(userInfo);
            if (ok != null) return ok;
            UserInfo user = new UserInfo();
            user.setName(userInfo.getName());
            user.setEmail(userInfo.getEmail());
            user.setPassword(passwordEncoder.encode(userInfo.getPassword()));
            user.setRole(userInfo.getRole());
            mongoTemplate.save(user);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, "User added successfully", userInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> login(RequestModel model) {
        try {
            // Authenticate username & password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(model.getUserName(), model.getPassword())
            );
            // Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Build a success response (token generation can be added here if needed)

            if(authentication.isAuthenticated()){
                String token =  jwtUtil.generateToken(userDetails);
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(
                                APIResponseEntity.Status.SUCCESS,
                                "Login successful",
                                token
                        ));
            }
            return null;

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(StatusCode.UNAUTHORIZED)
                    .body(new APIResponseEntity<>(
                            APIResponseEntity.Status.FAILED,
                            "Invalid credentials",
                            null
                    ));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR)
                    .body(new APIResponseEntity<>(
                            APIResponseEntity.Status.FAILED,
                            "An error occurred during login",
                            null
                    ));
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo userInfo = mongoTemplate.findOne(Query.query(Criteria.where(UserInfo.Fields.email).is(username)), UserInfo.class);

        if(userInfo == null){
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return new User(userInfo.getEmail(),userInfo.getPassword(), Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole())));
    }


    public static ResponseEntity<APIResponseEntity<?>> getUserInfoResponse(UserInfo userInfo) {
        StringBuilder errorMessage = new StringBuilder();

        if (userInfo.getName() == null || userInfo.getName().isEmpty()) {
            errorMessage.append("name is null or empty. ");
        }
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            errorMessage.append("email is null or empty. ");
        }

        if (userInfo.getPassword() == null || userInfo.getPassword().isEmpty()) {
            errorMessage.append("password is null or empty. ");
        }

        if (userInfo.getRole() == null || userInfo.getRole().isEmpty()) {
            errorMessage.append("role is null or empty. ");
        }

        if (!errorMessage.isEmpty()) {
            return ResponseEntity.status(StatusCode.NOT_FOUND)
                    .body(new APIResponseEntity<>(
                            APIResponseEntity.Status.FAILED,
                            errorMessage.toString().trim(),
                            "Failed to get data from the DB"
                    ));
        }

        return null;
    }
}
