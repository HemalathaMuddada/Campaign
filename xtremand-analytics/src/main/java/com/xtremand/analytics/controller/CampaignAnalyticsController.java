package com.xtremand.analytics.controller;

import com.xtremand.analytics.service.CampaignAnalyticsService;
import com.xtremand.domain.dto.CampaignAnalyticsDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/campaigns")
public class CampaignAnalyticsController {

    private final CampaignAnalyticsService campaignAnalyticsService;

    public CampaignAnalyticsController(CampaignAnalyticsService campaignAnalyticsService) {
        this.campaignAnalyticsService = campaignAnalyticsService;
    }

    @GetMapping
    public CampaignAnalyticsDto getCampaignAnalytics() {
        return campaignAnalyticsService.getCampaignAnalytics();
    }
}
