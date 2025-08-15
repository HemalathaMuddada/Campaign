package com.xtremand.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.xtremand.domain.enums.CampaignType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "xt_campaign")
public class Campaign {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="name")
	private String name;
	
	@Column(name="type")
	@Enumerated(EnumType.STRING)
	private CampaignType type;
	
	@Column(name="contentStrategy")
	private String contentStrategy;
	
	@Column(name="scheduledAt")
	private LocalDateTime scheduledAt;
	
	@Column(name="aiPersonalization")
	private boolean aiPersonalization;
	
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "is_sent")
    private boolean sent = false;
	
	@ManyToOne
	@JoinColumn(name = "contact_list_id")
	private ContactList contactList;
	
	@ManyToOne
	@JoinColumn(name = "email_template_id")
	private EmailTemplate emailTemplate;


}
