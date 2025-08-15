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

    long countByBouncedAtIsNotNull();

    long countByClickedAtIsNotNull();

    @org.springframework.data.jpa.repository.Query("SELECT new com.xtremand.domain.dto.CampaignAnalyticsDto.MonthlyPerformance(MONTH(e.sentAt), " +
            "SUM(CASE WHEN e.sentAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.sentAt IS NOT NULL AND e.bouncedAt IS NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.openedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.clickedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.repliedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.bouncedAt IS NOT NULL THEN 1 ELSE 0 END)) " +
            "FROM EmailAnalytics e " +
            "GROUP BY MONTH(e.sentAt)")
    java.util.List<com.xtremand.domain.dto.CampaignAnalyticsDto.MonthlyPerformance> findMonthlyPerformance();

    @org.springframework.data.jpa.repository.Query("SELECT new com.xtremand.domain.dto.CampaignAnalyticsDto.HourlyData(HOUR(e.openedAt), " +
            "SUM(CASE WHEN e.openedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.clickedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.repliedAt IS NOT NULL THEN 1 ELSE 0 END)) " +
            "FROM EmailAnalytics e " +
            "GROUP BY HOUR(e.openedAt)")
    java.util.List<com.xtremand.domain.dto.CampaignAnalyticsDto.HourlyData> findHourlyPerformance();

    @org.springframework.data.jpa.repository.Query("SELECT new com.xtremand.domain.dto.CampaignAnalyticsDto.PerformanceMetrics(" +
            "SUM(CASE WHEN e.sentAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.sentAt IS NOT NULL AND e.bouncedAt IS NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.openedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.clickedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.repliedAt IS NOT NULL THEN 1 ELSE 0 END)) " +
            "FROM EmailAnalytics e " +
            "WHERE e.device = :device")
    com.xtremand.domain.dto.CampaignAnalyticsDto.PerformanceMetrics findDevicePerformance(String device);

    @org.springframework.data.jpa.repository.Query("SELECT new com.xtremand.domain.dto.CampaignAnalyticsDto.CountryPerformance(e.country, " +
            "SUM(CASE WHEN e.sentAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.sentAt IS NOT NULL AND e.bouncedAt IS NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.openedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.clickedAt IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN e.repliedAt IS NOT NULL THEN 1 ELSE 0 END)) " +
            "FROM EmailAnalytics e " +
            "GROUP BY e.country")
    java.util.List<com.xtremand.domain.dto.CampaignAnalyticsDto.CountryPerformance> findCountryPerformance();
}
