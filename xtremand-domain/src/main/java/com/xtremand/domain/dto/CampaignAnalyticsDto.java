package com.xtremand.domain.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CampaignAnalyticsDto {
    private EmailCampaignMetrics emailCampaignMetrics;

    @Data
    public static class EmailCampaignMetrics {
        private Overview overview;
        private List<MonthlyPerformance> monthlyPerformance;
        private List<CampaignComparison> campaignComparison;
        private List<DeliveryStatus> deliveryStatus;
        private List<HourlyData> hourlyData;
        private DevicePerformance devicePerformance;
        private List<CountryPerformance> countryPerformance;
        private Filters filters;
        private Metadata metadata;
    }

    @Data
    public static class Overview {
        private long totalSent;
        private long totalDelivered;
        private long totalOpened;
        private long totalClicked;
        private long totalReplied;
        private long totalBounced;
        private double openRate;
        private double clickRate;
        private double replyRate;
        private double deliveryRate;
        private double bounceRate;
    }

    @Data
    public static class MonthlyPerformance {
        private String month;
        private int monthIndex;
        private long sent;
        private long delivered;
        private long opened;
        private long clicked;
        private long replied;
        private long bounced;
        private double openRate;
        private double clickRate;
        private double replyRate;

        public MonthlyPerformance(int monthIndex, long sent, long delivered, long opened, long clicked, long replied, long bounced) {
            this.monthIndex = monthIndex;
            this.sent = sent;
            this.delivered = delivered;
            this.opened = opened;
            this.clicked = clicked;
            this.replied = replied;
            this.bounced = bounced;
        }
    }

    @Data
    public static class CampaignComparison {
        private String name;
        private long sent;
        private long opened;
        private long clicked;
        private long replied;
        private double openRate;
        private double clickRate;
        private double replyRate;
    }

    @Data
    public static class DeliveryStatus {
        private String type;
        private double value;
        private String color;
    }

    @Data
    public static class HourlyData {
        private String hour;
        private int hourIndex;
        private long opens;
        private long clicks;
        private long replies;

        public HourlyData(int hourIndex, long opens, long clicks, long replies) {
            this.hourIndex = hourIndex;
            this.opens = opens;
            this.clicks = clicks;
            this.replies = replies;
        }
    }

    @Data
    public static class DevicePerformance {
        private PerformanceMetrics mobile;
        private PerformanceMetrics desktop;
    }

    @Data
    public static class PerformanceMetrics {
        private long sent;
        private long delivered;
        private long opened;
        private long clicked;
        private long replied;
        private double openRate;
        private double clickRate;
        private double replyRate;
        private double percentage;

        public PerformanceMetrics(long sent, long delivered, long opened, long clicked, long replied) {
            this.sent = sent;
            this.delivered = delivered;
            this.opened = opened;
            this.clicked = clicked;
            this.replied = replied;
        }
    }

    @Data
    public static class CountryPerformance {
        private String country;
        private long sent;
        private long delivered;
        private long opened;
        private long clicked;
        private long replied;
        private double openRate;
        private double clickRate;
        private double replyRate;

        public CountryPerformance(String country, long sent, long delivered, long opened, long clicked, long replied) {
            this.country = country;
            this.sent = sent;
            this.delivered = delivered;
            this.opened = opened;
            this.clicked = clicked;
            this.replied = replied;
        }
    }

    @Data
    public static class Filters {
        private List<String> timeRanges;
        private List<String> campaigns;
        private List<String> countries;
        private List<String> devices;
        private List<String> timeOfDay;
    }

    @Data
    public static class Metadata {
        private long totalRecords;
        private String lastUpdated;
    }
}
