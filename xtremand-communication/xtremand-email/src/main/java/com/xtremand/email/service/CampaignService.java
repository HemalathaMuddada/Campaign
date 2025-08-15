package com.xtremand.email.service;

import com.xtremand.domain.dto.CampaignCreateRequest;
import com.xtremand.domain.dto.CampaignResponse;
import com.xtremand.domain.dto.Response;
import com.xtremand.domain.entity.Campaign;
import com.xtremand.domain.entity.CampaignDashboardStats;
import com.xtremand.domain.entity.ContactList;
import com.xtremand.domain.entity.EmailTemplate;
import com.xtremand.domain.enums.EmailStatus;
import com.xtremand.email.repository.CampaignRepository;
import com.xtremand.email.repository.EmailAnalyticsRepository;
import com.xtremand.email.repository.EmailTemplateRepository;
import com.xtremand.contact.repository.ContactListRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CampaignService {
	private final CampaignRepository campaignRepo;
	private final ContactListRepository contactListRepo;
	private final EmailService emailService;
	private EmailTemplateRepository emailTemplateRepository;
    private final EmailAnalyticsRepository analyticsrepo;


	public CampaignService(CampaignRepository campaignRepo, ContactListRepository contactListRepo,
			EmailService emailService, EmailTemplateRepository emailTemplateRepository,
			EmailAnalyticsRepository analyticsrepo) {
		this.campaignRepo = campaignRepo;
		this.contactListRepo = contactListRepo;
		this.emailService = emailService;
		this.emailTemplateRepository = emailTemplateRepository;
		this.analyticsrepo = analyticsrepo;
	}

	public Campaign createCampaign(CampaignCreateRequest req) {
		ContactList contactList = contactListRepo.findById(req.getContactListId())
				.orElseThrow(() -> new RuntimeException("Contact list not found"));
		EmailTemplate emailTemplateList = emailTemplateRepository.findById(req.getContactListId())
				.orElseThrow(() -> new RuntimeException("template list not found"));
		Campaign campaign = new Campaign();
		campaign.setName(req.getName());
		campaign.setType(req.getType());
		campaign.setContactList(contactList);
		campaign.setEmailTemplate(emailTemplateList);
		campaign.setScheduledAt(req.getScheduledAt());
		campaign.setCreatedAt(LocalDate.now());
		campaign.setContentStrategy(req.getContentStrategy());
		campaign.setAiPersonalization(req.isAiPersonalization());
		campaign = campaignRepo.save(campaign);
		return campaign;
	}

	public Campaign updateCampaign(Long id, CampaignCreateRequest req) {
		Campaign existingCampaign = campaignRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Campaign not found"));
		ContactList contactList = contactListRepo.findById(req.getContactListId())
				.orElseThrow(() -> new RuntimeException("Contact list not found"));
		EmailTemplate emailTemplate = emailTemplateRepository.findById(req.getTemplateId())
				.orElseThrow(() -> new RuntimeException("Template not found"));
		existingCampaign.setName(req.getName());
		existingCampaign.setType(req.getType());
		existingCampaign.setContactList(contactList);
		existingCampaign.setEmailTemplate(emailTemplate);
		existingCampaign.setScheduledAt(req.getScheduledAt());
		existingCampaign.setContentStrategy(req.getContentStrategy());
		existingCampaign.setAiPersonalization(req.isAiPersonalization());
		return campaignRepo.save(existingCampaign);
	}

	public void deleteCampaign(Long id) {
		if (!campaignRepo.existsById(id)) {
			throw new RuntimeException("Campaign not found");
		}
		campaignRepo.deleteById(id);
	}

	public List<CampaignResponse> getAllCampaignsWithAnalytics() {
	    List<Campaign> campaigns = campaignRepo.findAll();
	    List<CampaignResponse> responses = new ArrayList<>();
	    for (Campaign c : campaigns) {
	        CampaignResponse resp = new CampaignResponse();
	        resp.setId(c.getId());
	        resp.setName(c.getName());
	        resp.setCreatedAt(c.getCreatedAt());
	        resp.setSent(analyticsrepo.countByCampaignIdAndSentAtIsNotNull(c.getId()));
	        resp.setDelivered(analyticsrepo.countByCampaignIdAndSentAtIsNotNull(c.getId()));
	        resp.setOpened(analyticsrepo.countByCampaignIdAndOpenedAtIsNotNull(c.getId()));
	        resp.setClicked(analyticsrepo.countByCampaignIdAndClickedAtIsNotNull(c.getId()));
	        resp.setReplied(analyticsrepo.countByCampaignIdAndRepliedAtIsNotNull(c.getId()));
	        resp.setBounced(analyticsrepo.countByCampaignIdAndBouncedAtIsNotNull(c.getId()));
			if (c.getContactList() != null) {
				resp.setContactListName(c.getContactList().getName());
				int count = c.getContactList().getContacts() != null ? c.getContactList().getContacts().size() : 0;
				resp.setNumberOfContacts(count);
			} else {
				resp.setContactListName(null);
				resp.setNumberOfContacts(0);
			}
	        responses.add(resp);
	    }
	    return responses;
	}

	
	
	@Scheduled(fixedRate = 60000)
	public void sendScheduledCampaigns() {
		List<Campaign> campaignsToSend = campaignRepo.findByScheduledAtBeforeAndSentFalse(LocalDateTime.now());
		for (Campaign campaign : campaignsToSend) {
			launchCampaign(campaign);
		}
	}
	 
	 public String launchCampaign(Campaign campaign) {
		 ContactList contactList = campaign.getContactList();
         EmailTemplate template = campaign.getEmailTemplate();
         emailService.sendBulkEmailToList(
             contactList,
             template.getSubjectLine(),  
             template.getContent(),
             campaign
         );
         campaign.setSent(true); 
         campaignRepo.save(campaign);
         return "Campaign Launched Successfully";
	 }
	 
		public CampaignDashboardStats getCampaignDashboardStats() {
			long totalCampaigns = campaignRepo.count();
			long activeCampaigns = campaignRepo.countBySentFalse(); // is_sent = false
			long totalSent = analyticsrepo.countBySentAtIsNotNull();
			long totalOpened = analyticsrepo.countByOpenedAtIsNotNull();
			long totalReplied = analyticsrepo.countByRepliedAtIsNotNull();
			double responseRate = 0.0;
			if (totalSent > 0) {
				responseRate = ((double) (totalOpened + totalReplied) / totalSent) * 100;
			}
			CampaignDashboardStats stats = new CampaignDashboardStats();
			stats.setTotalCampaigns(totalCampaigns);
			stats.setActiveCampaigns(activeCampaigns);
			stats.setTotalSent(totalSent);
			stats.setResponseRate(responseRate);

			return stats;
		}
}
