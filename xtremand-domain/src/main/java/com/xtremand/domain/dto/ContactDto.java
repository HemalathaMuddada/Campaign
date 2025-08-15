package com.xtremand.domain.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class ContactDto {
	
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String jobTitle;
    private String company;
    private String location;
    private String tags;
    private String createdBy;
    private String updatedBy;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private boolean isActive;

}
