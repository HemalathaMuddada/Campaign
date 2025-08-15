package com.xtremand.email.service;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xtremand.ai.OllamaService;

@Component

public class EmailAutoReply extends RouteBuilder {

	@Value("${email.username}")
	private String emailUser;

	@Value("${email.password}")
	private String emailPassword;

	private final OllamaService ollamaService;

	public EmailAutoReply(OllamaService ollamaService) {
		this.ollamaService = ollamaService;
	}

	@Override
	public void configure() throws Exception {
		onException(Exception.class).log("❌ ERROR while sending email: ${exception.message}").handled(true);
		from("imaps://{{mail.host}}:{{mail.port}}?" + "username=" + emailUser + "&password=" + emailPassword
				+ "&delete={{mail.delete}}" + "&unseen={{mail.unseen}}" + "&fetchSize={{mail.fetchSize}}"
				+ "&initialDelay={{mail.initialDelay}}" + "&delay={{mail.delay}}").routeId("mail-auto-responder")
				.log("📥 Processing email from ${header.from} with subject ${header.subject}").process(exchange -> {
					String from = exchange.getIn().getHeader("From", String.class);
					if (from.contains("<")) {
						from = from.substring(from.indexOf('<') + 1, from.indexOf('>')).trim();
					}
					String subject = exchange.getIn().getHeader("Subject", String.class);
					String originalBody = exchange.getIn().getBody(String.class);
					String prompt = "You received the following email:\n\nSubject: " + subject + "\n\nBody:\n"
							+ originalBody + "\n\nPlease draft a polite and relevant email reply.";
					String aiResponseJson = ollamaService.getModelResponse(prompt);
					String replyBody;
					try {
						var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
						var jsonNode = mapper.readTree(aiResponseJson);
						replyBody = jsonNode.has("body") ? jsonNode.get("body").asText() : aiResponseJson;
					} catch (Exception e) {
						replyBody = aiResponseJson;
					}
					exchange.getMessage().setHeader("To", from);
					exchange.getMessage().setHeader("From", emailUser);
					exchange.getMessage().setHeader("Subject", "Re: " + subject);
					exchange.getMessage().setHeader("Content-Type", "text/plain; charset=UTF-8");
					exchange.getMessage().setBody(replyBody);
				})
				.to("smtps://{{smtp.host}}:{{smtp.port}}?" + "username=" + emailUser + "&password=" + emailPassword
						+ "&mail.smtp.auth=true" + "&mail.smtp.ssl.enable=true" + "&mail.debug=true")
				.log("✅ Auto-reply sent to ${header.To}");
	}

}
