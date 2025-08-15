package com.xtremand.email.service;

import com.xtremand.domain.dto.EmailAnalyticsSummaryDto;
import com.xtremand.domain.dto.EmailHistoryDto;
import com.xtremand.domain.entity.Campaign;
import com.xtremand.domain.entity.EmailAnalytics;
import com.xtremand.domain.enums.EmailStatus;
import com.xtremand.email.repository.EmailAnalyticsRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class EmailAnalyticsService {

	@Value("${email.tracking.url}")
	private String trackingUrl;

	private final EmailAnalyticsRepository repo;

	public EmailAnalyticsService(EmailAnalyticsRepository repo) {
		this.repo = repo;
	}

	public EmailAnalytics logEmailSent(Long contactId, String email, String subject,UUID trackingId, Campaign campaign) {
		EmailAnalytics analytics = new EmailAnalytics();
		analytics.setContactId(contactId);
		analytics.setEmail(email);
		analytics.setSubject(subject);
		analytics.setStatus(EmailStatus.SENT);
		analytics.setSentAt(LocalDateTime.now());
		trackingId = UUID.randomUUID();
		analytics.setTrackingId(trackingId);
		String trackedLink = trackingUrl.replace("{trackingId}", trackingId.toString());
		analytics.setTrackingUrl(trackedLink);
		analytics.setCampaign(campaign);
		return repo.save(analytics);
	}

	public void logEmailNotSent(Long contactId, String email, String subject, UUID trackingId, Campaign campaign) {
		EmailAnalytics analytics = new EmailAnalytics();
		analytics.setContactId(contactId);
		analytics.setEmail(email);
		analytics.setSubject(subject);
		analytics.setStatus(EmailStatus.NOT_SENT);
		analytics.setCampaign(campaign);
		repo.save(analytics);
	}

	public void logOpened(UUID trackingId) {
		repo.findByTrackingId(trackingId).ifPresent(a -> {
			a.setStatus(EmailStatus.OPENED);
			a.setOpenedAt(LocalDateTime.now());
			repo.save(a);
		});
	}

	public void logClicked(UUID trackingId) {
		repo.findByTrackingId(trackingId).ifPresent(a -> {
			a.setStatus(EmailStatus.CLICKED);
			a.setClickedAt(LocalDateTime.now());
			repo.save(a);
		});
	}

	public void logBounced(UUID trackingId) {
		repo.findByTrackingId(trackingId).ifPresent(a -> {
			a.setStatus(EmailStatus.BOUNCED);
			a.setBouncedAt(LocalDateTime.now());
			repo.save(a);
		});
	}

	public void logReplied(UUID trackingId) {
		repo.findByTrackingId(trackingId).ifPresent(a -> {
			a.setStatus(EmailStatus.REPLIED);
			a.setRepliedAt(LocalDateTime.now());
			repo.save(a);
		});
	}
	
	public EmailAnalyticsSummaryDto getEmailAnalyticsSummary() {
		List<EmailAnalytics> allRecords = repo.findAll();
		long totalEmails = allRecords.size();
		long sentCount = allRecords.stream().filter(r -> r.getSentAt() != null).count();
		long openedCount = allRecords.stream().filter(r -> r.getOpenedAt() != null).count();
		long clickedCount = allRecords.stream().filter(r -> r.getClickedAt() != null).count();
		long bouncedCount = allRecords.stream().filter(r -> r.getBouncedAt() != null).count();
		long notSentCount = totalEmails - sentCount;
		double openRate = sentCount == 0 ? 0 : (double) openedCount / sentCount * 100;
		double clickRate = sentCount == 0 ? 0 : (double) clickedCount / sentCount * 100;
		double bounceRate = sentCount == 0 ? 0 : (double) bouncedCount / sentCount * 100;
		double notSentRate = totalEmails == 0 ? 0 : (double) notSentCount / totalEmails * 100;
		return new EmailAnalyticsSummaryDto(totalEmails, sentCount, notSentCount, openedCount, clickedCount,
				bouncedCount, openRate, bounceRate, notSentRate, clickRate);
	}
	
	
	public List<EmailHistoryDto> getAllEmailHistory() {
		List<EmailAnalytics> allRecords = repo.findAll();
		List<EmailHistoryDto> history = new ArrayList<>();
		for (EmailAnalytics record : allRecords) {
			if (record.getSentAt() != null) {
				history.add(new EmailHistoryDto(record.getEmail(), EmailStatus.SENT, record.getSubject(),
						record.getSentAt()));
			}
			if (record.getStatus() == EmailStatus.NOT_SENT && record.getSentAt() == null) {
				history.add(new EmailHistoryDto(record.getEmail(), EmailStatus.NOT_SENT, record.getSubject(), null));
			}
		}
		history.sort(
				Comparator.comparing(EmailHistoryDto::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));
		return history;
	}

	

}
