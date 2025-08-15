package com.xtremand.analytics.service;

import com.xtremand.domain.dto.CampaignAnalyticsDto;
import com.xtremand.email.repository.CampaignRepository;
import com.xtremand.email.repository.EmailAnalyticsRepository;
import org.springframework.stereotype.Service;

@Service
public class CampaignAnalyticsService {

    private final EmailAnalyticsRepository emailAnalyticsRepository;
    private final CampaignRepository campaignRepository;

    public CampaignAnalyticsService(EmailAnalyticsRepository emailAnalyticsRepository, CampaignRepository campaignRepository) {
        this.emailAnalyticsRepository = emailAnalyticsRepository;
        this.campaignRepository = campaignRepository;
    }

    public CampaignAnalyticsDto getCampaignAnalytics() {
        CampaignAnalyticsDto campaignAnalyticsDto = new CampaignAnalyticsDto();
        CampaignAnalyticsDto.EmailCampaignMetrics emailCampaignMetrics = new CampaignAnalyticsDto.EmailCampaignMetrics();

        emailCampaignMetrics.setOverview(getOverview());
        emailCampaignMetrics.setMonthlyPerformance(getMonthlyPerformance());
        emailCampaignMetrics.setCampaignComparison(getCampaignComparison());
        emailCampaignMetrics.setDeliveryStatus(getDeliveryStatus());
        emailCampaignMetrics.setHourlyData(getHourlyData());
        emailCampaignMetrics.setDevicePerformance(getDevicePerformance());
        emailCampaignMetrics.setCountryPerformance(getCountryPerformance());
        emailCampaignMetrics.setFilters(getFilters());
        emailCampaignMetrics.setMetadata(getMetadata());

        campaignAnalyticsDto.setEmailCampaignMetrics(emailCampaignMetrics);
        return campaignAnalyticsDto;
    }

    private CampaignAnalyticsDto.Filters getFilters() {
        CampaignAnalyticsDto.Filters filters = new CampaignAnalyticsDto.Filters();
        filters.setTimeRanges(java.util.Arrays.asList("Last Week", "Last Month", "Last 6 Months", "Last Year", "Last 2 Years"));
        filters.setCampaigns(java.util.Arrays.asList("All Campaigns", "Welcome Series", "Product Demo", "Newsletter", "Re-engagement", "Follow-up", "Promotional"));
        filters.setCountries(java.util.Arrays.asList("All Countries", "US", "UK", "CA", "AU", "DE", "FR", "IN", "BR", "JP", "SG"));
        filters.setDevices(java.util.Arrays.asList("All Devices", "Desktop", "Mobile"));
        filters.setTimeOfDay(java.util.Arrays.asList("All Day", "AM", "PM"));
        return filters;
    }

    private CampaignAnalyticsDto.Metadata getMetadata() {
        CampaignAnalyticsDto.Metadata metadata = new CampaignAnalyticsDto.Metadata();
        metadata.setTotalRecords(emailAnalyticsRepository.count());
        metadata.setLastUpdated(java.time.LocalDateTime.now().toString());
        return metadata;
    }

    private CampaignAnalyticsDto.DevicePerformance getDevicePerformance() {
        CampaignAnalyticsDto.DevicePerformance devicePerformance = new CampaignAnalyticsDto.DevicePerformance();
        CampaignAnalyticsDto.PerformanceMetrics mobile = emailAnalyticsRepository.findDevicePerformance("mobile");
        CampaignAnalyticsDto.PerformanceMetrics desktop = emailAnalyticsRepository.findDevicePerformance("desktop");
        long totalSent = mobile.getSent() + desktop.getSent();

        mobile.setOpenRate(calculateRate(mobile.getOpened(), mobile.getDelivered()));
        mobile.setClickRate(calculateRate(mobile.getClicked(), mobile.getDelivered()));
        mobile.setReplyRate(calculateRate(mobile.getReplied(), mobile.getDelivered()));
        mobile.setPercentage(calculateRate(mobile.getSent(), totalSent));

        desktop.setOpenRate(calculateRate(desktop.getOpened(), desktop.getDelivered()));
        desktop.setClickRate(calculateRate(desktop.getClicked(), desktop.getDelivered()));
        desktop.setReplyRate(calculateRate(desktop.getReplied(), desktop.getDelivered()));
        desktop.setPercentage(calculateRate(desktop.getSent(), totalSent));

        devicePerformance.setMobile(mobile);
        devicePerformance.setDesktop(desktop);
        return devicePerformance;
    }

    private java.util.List<CampaignAnalyticsDto.CountryPerformance> getCountryPerformance() {
        java.util.List<CampaignAnalyticsDto.CountryPerformance> countryPerformanceList = emailAnalyticsRepository.findCountryPerformance();
        for (CampaignAnalyticsDto.CountryPerformance cp : countryPerformanceList) {
            cp.setOpenRate(calculateRate(cp.getOpened(), cp.getDelivered()));
            cp.setClickRate(calculateRate(cp.getClicked(), cp.getDelivered()));
            cp.setReplyRate(calculateRate(cp.getReplied(), cp.getDelivered()));
        }
        return countryPerformanceList;
    }

    private java.util.List<CampaignAnalyticsDto.HourlyData> getHourlyData() {
        java.util.List<CampaignAnalyticsDto.HourlyData> hourlyDataList = emailAnalyticsRepository.findHourlyPerformance();
        for (CampaignAnalyticsDto.HourlyData hd : hourlyDataList) {
            hd.setHour(getHourName(hd.getHourIndex()));
        }
        return hourlyDataList;
    }

    private String getHourName(int hourIndex) {
        if (hourIndex == 0) {
            return "12AM";
        } else if (hourIndex < 12) {
            return hourIndex + "AM";
        } else if (hourIndex == 12) {
            return "12PM";
        } else {
            return (hourIndex - 12) + "PM";
        }
    }

    private java.util.List<CampaignAnalyticsDto.DeliveryStatus> getDeliveryStatus() {
        java.util.List<CampaignAnalyticsDto.DeliveryStatus> deliveryStatusList = new java.util.ArrayList<>();
        long totalSent = emailAnalyticsRepository.countBySentAtIsNotNull();
        long totalBounced = emailAnalyticsRepository.countByBouncedAtIsNotNull();
        long totalDelivered = totalSent - totalBounced;

        CampaignAnalyticsDto.DeliveryStatus delivered = new CampaignAnalyticsDto.DeliveryStatus();
        delivered.setType("Delivered");
        delivered.setValue(calculateRate(totalDelivered, totalSent));
        delivered.setColor("#0ea5e9");
        deliveryStatusList.add(delivered);

        CampaignAnalyticsDto.DeliveryStatus bounced = new CampaignAnalyticsDto.DeliveryStatus();
        bounced.setType("Bounced");
        bounced.setValue(calculateRate(totalBounced, totalSent));
        bounced.setColor("#ef4444");
        deliveryStatusList.add(bounced);

        return deliveryStatusList;
    }

    private java.util.List<CampaignAnalyticsDto.CampaignComparison> getCampaignComparison() {
        java.util.List<com.xtremand.domain.entity.Campaign> campaigns = campaignRepository.findAll();
        java.util.List<CampaignAnalyticsDto.CampaignComparison> campaignComparisons = new java.util.ArrayList<>();
        for (com.xtremand.domain.entity.Campaign campaign : campaigns) {
            CampaignAnalyticsDto.CampaignComparison cc = new CampaignAnalyticsDto.CampaignComparison();
            cc.setName(campaign.getName());
            long sent = emailAnalyticsRepository.countByCampaignIdAndSentAtIsNotNull(campaign.getId());
            long bounced = emailAnalyticsRepository.countByCampaignIdAndBouncedAtIsNotNull(campaign.getId());
            long delivered = sent - bounced;
            long opened = emailAnalyticsRepository.countByCampaignIdAndOpenedAtIsNotNull(campaign.getId());
            long clicked = emailAnalyticsRepository.countByCampaignIdAndClickedAtIsNotNull(campaign.getId());
            long replied = emailAnalyticsRepository.countByCampaignIdAndRepliedAtIsNotNull(campaign.getId());

            cc.setSent(sent);
            cc.setOpened(opened);
            cc.setClicked(clicked);
            cc.setReplied(replied);

            cc.setOpenRate(calculateRate(opened, delivered));
            cc.setClickRate(calculateRate(clicked, delivered));
            cc.setReplyRate(calculateRate(replied, delivered));
            campaignComparisons.add(cc);
        }
        return campaignComparisons;
    }

    private java.util.List<CampaignAnalyticsDto.MonthlyPerformance> getMonthlyPerformance() {
        java.util.List<CampaignAnalyticsDto.MonthlyPerformance> monthlyPerformanceList = emailAnalyticsRepository.findMonthlyPerformance();
        for (CampaignAnalyticsDto.MonthlyPerformance mp : monthlyPerformanceList) {
            mp.setMonth(getMonthName(mp.getMonthIndex()));
            mp.setOpenRate(calculateRate(mp.getOpened(), mp.getDelivered()));
            mp.setClickRate(calculateRate(mp.getClicked(), mp.getDelivered()));
            mp.setReplyRate(calculateRate(mp.getReplied(), mp.getDelivered()));
        }
        return monthlyPerformanceList;
    }

    private String getMonthName(int monthIndex) {
        java.text.DateFormatSymbols dfs = new java.text.DateFormatSymbols();
        return dfs.getShortMonths()[monthIndex - 1];
    }

    private CampaignAnalyticsDto.Overview getOverview() {
        CampaignAnalyticsDto.Overview overview = new CampaignAnalyticsDto.Overview();
        long totalSent = emailAnalyticsRepository.countBySentAtIsNotNull();
        long totalBounced = emailAnalyticsRepository.countByBouncedAtIsNotNull();
        long totalDelivered = totalSent - totalBounced;
        long totalOpened = emailAnalyticsRepository.countByOpenedAtIsNotNull();
        long totalClicked = emailAnalyticsRepository.countByClickedAtIsNotNull();
        long totalReplied = emailAnalyticsRepository.countByRepliedAtIsNotNull();

        overview.setTotalSent(totalSent);
        overview.setTotalDelivered(totalDelivered);
        overview.setTotalOpened(totalOpened);
        overview.setTotalClicked(totalClicked);
        overview.setTotalReplied(totalReplied);
        overview.setTotalBounced(totalBounced);

        overview.setOpenRate(calculateRate(totalOpened, totalDelivered));
        overview.setClickRate(calculateRate(totalClicked, totalDelivered));
        overview.setReplyRate(calculateRate(totalReplied, totalDelivered));
        overview.setDeliveryRate(calculateRate(totalDelivered, totalSent));
        overview.setBounceRate(calculateRate(totalBounced, totalSent));

        return overview;
    }

    private double calculateRate(long numerator, long denominator) {
        if (denominator == 0) {
            return 0.0;
        }
        return ((double) numerator / denominator) * 100;
    }
}
