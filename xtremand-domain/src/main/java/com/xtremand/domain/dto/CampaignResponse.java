package com.xtremand.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.xtremand.domain.entity.CampaignDashboardStats;

import lombok.Data;

@Data
public class CampaignResponse {
    private Long id;
    private String name;
    private int sent;
    private int delivered;
    private int opened;
    private int clicked;
    private int replied;
    private int bounced;
    private LocalDate createdAt;
    private String contactListName;
    private int numberOfContacts;
    private CampaignDashboardStats dashboardStats;
}

