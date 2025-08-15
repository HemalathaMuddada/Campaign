package com.xtremand.email.service;

import com.xtremand.contact.service.ContactService;
import com.xtremand.domain.dto.ContactDto;
import com.xtremand.domain.dto.SendEmailRequest;
import com.xtremand.domain.entity.Campaign;
import com.xtremand.domain.entity.Contact;
import com.xtremand.domain.entity.ContactList;
import com.xtremand.domain.entity.EmailAnalytics;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailService {

	@Value("${email.tracking.url}")
	private String trackingUrl;
	
	private final ProducerTemplate producerTemplate;
	private final ContactService contactService;
	private final EmailAnalyticsService analyticsService;

	@Value("${email.username}")
	private String fromEmail;

	public EmailService(ProducerTemplate producerTemplate, ContactService contactService,
			EmailAnalyticsService analyticsService) {
		this.producerTemplate = producerTemplate;
		this.contactService = contactService;
		this.analyticsService = analyticsService;
	}

	public void sendBulkEmail(SendEmailRequest request,Campaign campaign) {
		for (Long contactId : request.getContactIds()) {
			ContactDto contact = contactService.getContactById(contactId);
			UUID trackingId = null;
			if (contact != null && contact.getEmail() != null && !contact.getEmail().isEmpty()) {
				try {
					EmailAnalytics emailAnalytics =analyticsService.logEmailSent(contact.getId(), contact.getEmail(),
							request.getSubject(),trackingId,campaign);
					String personalizedBody = personalizeBody(request.getBody(), contact, emailAnalytics.getTrackingId());
					sendEmail(request.getSubject(), personalizedBody, contact.getEmail());
				} catch (Exception e) {
					analyticsService.logEmailNotSent(contactId, contact.getEmail(), request.getSubject(),
							trackingId,campaign);
				}
			}
		}
	}

	private String personalizeBody(String body, ContactDto contact, UUID trackingId) {
		String trackedPixel = "<img src=\"https://yourserver.com/track/open/" + trackingId
				+ "\" width=\"1\" height=\"1\" />";
		String trackedLink = trackingUrl.replace("{trackingId}", trackingId.toString());

		return body.replace("[Recipient's Name]", contact.getFirstName() + " " + contact.getLastName())
				.replace("{{email}}", contact.getEmail())
				.replace("[Name]", contact.getFirstName() + " " + contact.getLastName())
				.replace("[firstName]", contact.getFirstName()).replace("[Email]", contact.getEmail())
				.replace("[Company]", contact.getCompany()).replace("[Location]", contact.getLocation())
				.replace("[SenderEmail]", fromEmail) + "<br><a href=\"" + trackedLink + "\">Click Here</a>"
				+ trackedPixel;
	}

	private void sendEmail(String subject, String body, String to) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("To", to);
		headers.put("From", fromEmail);
		headers.put("Subject", subject);
		headers.put("Content-Type", "text/html; charset=UTF-8"); 

		producerTemplate.sendBodyAndHeaders("direct:sendEmail", body, headers);
	}
	
	public void sendBulkEmailToList(ContactList contactList, String subject, String body, Campaign campaign) {
	    SendEmailRequest req = new SendEmailRequest();
	    req.setSubject(subject);
	    req.setBody(body);
	    req.setContactIds(contactList.getContacts().stream().map(Contact::getId).toList());
	    sendBulkEmail(req,campaign);
	}

}
