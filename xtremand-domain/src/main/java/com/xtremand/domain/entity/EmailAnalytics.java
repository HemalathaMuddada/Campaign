package com.xtremand.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import com.xtremand.domain.enums.EmailStatus;

@Entity
@Table(name = "email_analytics")
@Getter
@Setter
public class EmailAnalytics {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long contactId;
	
    @Column(name = "email")
	private String email;
    
    @Column(name = "subject")
	private String subject;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private EmailStatus status;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;
	
	@Column(name = "opened_at")	
	private LocalDateTime openedAt;
	
	@Column(name = "clicked_at")
	private LocalDateTime clickedAt;
	
	@Column(name = "bounced_at")
	private LocalDateTime bouncedAt;
	
	@Column(name = "repliedAt")
	private LocalDateTime repliedAt;
	
	@Column(name = "trackingId")
	private UUID trackingId;
	
	@Column(name = "tracking_url")
	private String trackingUrl;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;
}
