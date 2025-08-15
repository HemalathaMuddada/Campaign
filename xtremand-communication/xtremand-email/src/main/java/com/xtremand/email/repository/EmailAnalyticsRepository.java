package com.xtremand.email.repository;

import com.xtremand.domain.entity.EmailAnalytics;
import com.xtremand.domain.enums.EmailStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailAnalyticsRepository extends JpaRepository<EmailAnalytics, Long> {
	
    Optional<EmailAnalytics> findByTrackingId(UUID trackingId);
    
    int countByCampaignIdAndSentAtIsNotNull(Long campaignId);
    int countByCampaignIdAndOpenedAtIsNotNull(Long campaignId);
    int countByCampaignIdAndClickedAtIsNotNull(Long campaignId);
    int countByCampaignIdAndRepliedAtIsNotNull(Long campaignId);
    int countByCampaignIdAndBouncedAtIsNotNull(Long campaignId);
    
    long countBySentAtIsNotNull();  

    long countByOpenedAtIsNotNull(); 

    long countByRepliedAtIsNotNull();


}
