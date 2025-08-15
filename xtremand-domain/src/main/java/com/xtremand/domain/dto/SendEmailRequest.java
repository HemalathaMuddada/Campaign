package com.xtremand.domain.dto;

import java.util.List;

import lombok.Data;


@Data
public class SendEmailRequest {

    private String subject;
    private String body;
    private List<Long> contactIds;

   
}
