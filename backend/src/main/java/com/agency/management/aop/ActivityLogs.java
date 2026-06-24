package com.agency.management.aop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Activity_Logs")
public class ActivityLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String message;

    private String loggedInUser;

    private String customerName;

    private String deliveryBoyName;


}
