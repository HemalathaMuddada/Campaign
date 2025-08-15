package com.xtremand.domain.entity;

import com.xtremand.domain.enums.EmailCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "email_templates")
@Getter
@Setter
public class EmailTemplate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="name")
	private String name;
	
	@Column(name="category")
	@Enumerated(EnumType.STRING)
	private EmailCategory category;
	
	@Column(name="subjectLine")
	private String subjectLine;

	@Column(name="content")
	private String content;

	@Column(name="variables")
	private String variables;

}
