package com.example.LeadManagement.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="activity_logs")
public class ActivityLogs {

    @Id
    private String id;

    // Which module generated this log like user or lead or project
    private EntityType entityType;

    // Id of User / Lead / Project that entity voda id .
    private String entityId;

    // CREATE / UPDATE / DELETE / ASSIGN it action happened the fetch or add something to see
    private ActionType actionType;

    // Description of activity
    private String message;

    // Who performed this action
    private String performedBy;

    // Activity created time
    private LocalDateTime createdAt;
    //old value
    private String oldValue;
//this will be the newValue coz , this to that changing requirements
    private String newValue;

}