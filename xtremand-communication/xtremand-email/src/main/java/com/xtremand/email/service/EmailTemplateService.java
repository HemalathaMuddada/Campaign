package com.xtremand.email.service;

import com.xtremand.domain.dto.EmailTemplateRequest;
import com.xtremand.domain.entity.EmailTemplate;
import com.xtremand.domain.enums.CampaignType;
import com.xtremand.domain.enums.EmailCampaignType;
import com.xtremand.domain.enums.EmailCategory;
import com.xtremand.domain.enums.Tone;
import com.xtremand.email.repository.EmailTemplateRepository;
import com.xtremand.email.specification.TemplateListSpecification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
	private final EmailTemplateRepository repository;

	public EmailTemplateService(EmailTemplateRepository repository) {
		this.repository = repository;
	}

	public EmailTemplate saveTemplate(EmailTemplateRequest dto) {
		EmailTemplate template = new EmailTemplate();
		template.setName(dto.getName());
		if (!EmailCategory.contains(dto.getCategory().name())) {
			throw new IllegalArgumentException("Category not found");
		}
		template.setCategory(dto.getCategory());
		template.setSubjectLine(dto.getSubjectLine());
		template.setContent(dto.getContent());
		template.setVariables(dto.getVariables());
		return repository.save(template);
	}

	public EmailTemplate getTemplateById(Long id) {
		return repository.findById(id).orElse(null);
	}

	public boolean deleteTemplateById(Long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	public EmailTemplate updateTemplate(Long id, EmailTemplateRequest dto) {
		return repository.findById(id).map(template -> {
			template.setName(dto.getName());
			if (!EmailCategory.contains(dto.getCategory().name())) {
				throw new IllegalArgumentException("Category not found");
			}
			template.setCategory(dto.getCategory());
			template.setSubjectLine(dto.getSubjectLine());
			template.setContent(dto.getContent());
			template.setVariables(dto.getVariables());
			return repository.save(template);
		}).orElse(null);
	}

	public Map<String, Object> getTemplatesWithStats(String category, String search) {
		List<EmailTemplate> templates = getAllTemplates(category, search); 
		long totalTemplates = templates.size();
		long distinctCategories = templates.stream().map(EmailTemplate::getCategory).filter(Objects::nonNull).distinct()
				.count();
		EmailCategory mostUsedCategory = templates.stream().filter(t -> t.getCategory() != null)
				.collect(Collectors.groupingBy(EmailTemplate::getCategory, Collectors.counting())).entrySet().stream()
				.max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
		Map<String, Object> result = new HashMap<>();
		result.put("totalTemplates", totalTemplates);
		result.put("distinctCategories", distinctCategories);
		result.put("mostUsedCategory", mostUsedCategory);
		result.put("templates", templates);
		return result;
	}

	public List<EmailTemplate> getAllTemplates(String category, String search) {
		Specification<EmailTemplate> spec = TemplateListSpecification.build(category, search);
		return repository.findAll(spec);
	}

	public List<String> getAllCategories() {
		return Arrays.stream(EmailCategory.values()).map(EmailCategory::getValue).collect(Collectors.toList());
	}

	public List<String> getAllCampaignTypes() {
		return Arrays.stream(EmailCampaignType.values()).map(EmailCampaignType::getValue).collect(Collectors.toList());
	}

	public List<String> getAllTones() {
		return Arrays.stream(Tone.values()).map(Tone::getValue).collect(Collectors.toList());
	}

}
