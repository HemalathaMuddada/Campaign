package com.xtremand.email.controller;

import com.xtremand.domain.dto.CampaignCreateRequest;
import com.xtremand.domain.dto.CampaignResponse;
import com.xtremand.domain.dto.CampaignsAnalyticsResponse;
import com.xtremand.domain.entity.Campaign;
import com.xtremand.domain.entity.CampaignDashboardStats;
import com.xtremand.email.repository.CampaignRepository;
import com.xtremand.email.service.CampaignService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {

	private final CampaignService campaignService;

	private final CampaignRepository repo;

	public CampaignController(CampaignService campaignService, CampaignRepository repo) {
		this.campaignService = campaignService;
		this.repo = repo;
	}

	@PostMapping
	public CampaignResponse createCampaign(@RequestBody CampaignCreateRequest request) {
		Campaign campaign = campaignService.createCampaign(request);
		CampaignResponse resp = new CampaignResponse();
		resp.setId(campaign.getId());
		resp.setName(campaign.getName());
		return resp;
	}

	@PutMapping("/{id}")
	public CampaignResponse updateCampaign(@PathVariable Long id, @RequestBody CampaignCreateRequest request) {
		Campaign updated = campaignService.updateCampaign(id, request);
		CampaignResponse resp = new CampaignResponse();
		resp.setId(updated.getId());
		resp.setName(updated.getName());
		return resp;
	}

	@DeleteMapping("/{id}")
	public void deleteCampaign(@PathVariable Long id) {
		campaignService.deleteCampaign(id);
	}

	@GetMapping
	public CampaignsAnalyticsResponse getAllCampaigns() {
		List<CampaignResponse> campaignList = campaignService.getAllCampaignsWithAnalytics();
		CampaignDashboardStats dashboardStats = campaignService.getCampaignDashboardStats();

		CampaignsAnalyticsResponse response = new CampaignsAnalyticsResponse();
		response.setCampaigns(campaignList);
		response.setDashboardStats(dashboardStats);

		return response;
	}

	@PostMapping("/launch")
	public String createCampaign(@PathVariable Long campaignId) {
		@SuppressWarnings("deprecation")
		Campaign campaign = repo.getOne(campaignId);
		String resp = campaignService.launchCampaign(campaign);
		return resp;
	}
}
