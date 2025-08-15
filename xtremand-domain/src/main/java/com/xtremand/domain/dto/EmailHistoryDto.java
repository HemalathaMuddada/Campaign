package com.xtremand.domain.dto;

import com.xtremand.domain.enums.EmailStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EmailHistoryDto {
    private String email;
    private EmailStatus status;
    private String subject;
    private LocalDateTime timestamp; 
}
