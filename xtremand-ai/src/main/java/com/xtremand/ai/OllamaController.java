package com.xtremand.ai;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xtremand.domain.dto.EmailRequest;

@RestController
@RequestMapping("/ai")
public class OllamaController {

	private final OllamaService ollamaService;

	public OllamaController(OllamaService ollamaService) {
		this.ollamaService = ollamaService;
	}

	@PostMapping("/generateEmail")
	public String generate(@RequestBody EmailRequest emailRequest) {
	    return ollamaService.getResponse(emailRequest);
	}


}
