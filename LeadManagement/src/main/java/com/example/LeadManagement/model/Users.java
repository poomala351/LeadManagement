package com.example.LeadManagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class Users{

    @Id
    //user id
    private String id;
    //user name
    private String name;
    //user email
    private String email;
    //user phone
    private String phone;
    //user status isActive/or inactive
    private Boolean isActive;
    //record created time
    private LocalDateTime createdAt;
    //record last updated time
    private LocalDateTime updatedAt;

}
