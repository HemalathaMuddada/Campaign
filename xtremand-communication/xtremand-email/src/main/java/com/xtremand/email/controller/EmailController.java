package com.xtremand.email.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xtremand.domain.dto.EmailAnalyticsSummaryDto;
import com.xtremand.domain.dto.EmailHistoryDto;
import com.xtremand.domain.dto.EmailTemplateRequest;
import com.xtremand.domain.dto.Response;
import com.xtremand.domain.dto.SendEmailRequest;
import com.xtremand.domain.entity.EmailTemplate;
import com.xtremand.email.service.EmailAnalyticsService;
import com.xtremand.email.service.EmailService;
import com.xtremand.email.service.EmailTemplateService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/emails")
public class EmailController {

	private final EmailService emailService;

	private final EmailTemplateService templateService;

	private final EmailAnalyticsService analyticsService;

	public EmailController(EmailService emailService, EmailTemplateService templateService,
			EmailAnalyticsService analyticsService) {
		this.emailService = emailService;
		this.templateService = templateService;
		this.analyticsService = analyticsService;
	}

	@PostMapping("/send")
	public ResponseEntity<Response> sendEmail(@RequestBody SendEmailRequest request) {
		emailService.sendBulkEmail(request,null);
		return ResponseEntity.ok(new Response("Mail sent successfully"));
	}

	@GetMapping("/open/{trackingId}")
	public void trackOpen(@PathVariable UUID trackingId, HttpServletResponse response) throws IOException {
		analyticsService.logOpened(trackingId);
		response.setContentType("image/gif");
		response.getOutputStream().write(new byte[] { 71, 73, 70, 56, 57, 97 }); 
		response.getOutputStream().flush();
	}

	@GetMapping("/click/{trackingId}")
	public void trackClick(@PathVariable UUID trackingId, @RequestParam("redirect") String redirect,
			HttpServletResponse response) throws IOException {
		analyticsService.logClicked(trackingId);
		response.sendRedirect(redirect);
	}
	
	@GetMapping("/analytics/summary")
	public ResponseEntity<EmailAnalyticsSummaryDto> getEmailAnalyticsSummary() {
	    EmailAnalyticsSummaryDto summary = analyticsService.getEmailAnalyticsSummary();
	    return ResponseEntity.ok(summary);
	}
	
	@GetMapping("/history")
	public ResponseEntity<List<EmailHistoryDto>> getEmailHistory() {
	    return ResponseEntity.ok(analyticsService.getAllEmailHistory());
	}

	@PostMapping("/email-templates")
	public ResponseEntity<Response> createTemplate(@RequestBody EmailTemplateRequest request) {
		EmailTemplate saved = templateService.saveTemplate(request);
		if (saved != null) {
			return ResponseEntity.ok(new Response("Added successfully"));
		} else {
			return ResponseEntity.status(500).body(new Response("Failed to add template"));
		}
	}

	@GetMapping("/email-templates/{id}")
	public ResponseEntity<EmailTemplate> getTemplateById(@PathVariable Long id) {
		EmailTemplate template = templateService.getTemplateById(id);
		if (template == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(template);
	}

	@DeleteMapping("/email-templates/{id}")
	public ResponseEntity<Response> deleteTemplate(@PathVariable Long id) {
		boolean deleted = templateService.deleteTemplateById(id);
		if (deleted) {
			return ResponseEntity.ok(new Response("Deleted successfully"));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/email-templates/{id}")
	public ResponseEntity<Response> updateTemplate(@PathVariable Long id, @RequestBody EmailTemplateRequest request) {
		EmailTemplate updated = templateService.updateTemplate(id, request);
		if (updated == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(new Response("Updated successfully"));
	}

	@GetMapping("/email-templates")
	public ResponseEntity<Map<String, Object>> getAllTemplates(@RequestParam(required = false) String category,
			@RequestParam(required = false) String search) {
		Map<String, Object> result = templateService.getTemplatesWithStats(category, search);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/email-templates/categories")
	public ResponseEntity<List<String>> getAllCategories() {
		List<String> categories = templateService.getAllCategories();
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/email-templates/campaignTypes")
	public ResponseEntity<List<String>> getAllCampaignTypes() {
		List<String> categories = templateService.getAllCampaignTypes();
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/email-templates/tones")
	public ResponseEntity<List<String>> getAllTones() {
		List<String> categories = templateService.getAllTones();
		return ResponseEntity.ok(categories);
	}

}
