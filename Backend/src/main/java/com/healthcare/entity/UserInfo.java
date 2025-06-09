package com.healthcare.entity;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@Document(collection = "users")
public class UserInfo {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean active;
}
