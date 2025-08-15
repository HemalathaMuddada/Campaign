package com.xtremand.contact.controller;

import com.xtremand.contact.service.ContactService;
import com.xtremand.domain.dto.*;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/contacts")
public class ContactController {

	private final ContactService contactService;

	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	@PostMapping("/upload")
	public ResponseEntity<Response> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam(name = "skipHeader", defaultValue = "true") boolean skipHeader) {
		if (file == null || file.isEmpty()) {
			return ResponseEntity.badRequest().body(new Response("File is missing or empty"));
		}
		try {
			contactService.save(file, skipHeader);
			return ResponseEntity.ok(new Response("File uploaded and contacts saved successfully"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new Response(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response("Failed to process the file: " + e.getMessage()));
		}
	}

	@PostMapping
	public ResponseEntity<Response> createList(@RequestBody ContactListRequestDto dto) {
		contactService.createList(dto.getName(), dto.getDescription());
		return ResponseEntity.ok(new Response("List created successfully"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Response> updateList(@PathVariable Long id, @RequestBody ContactListRequestDto dto) {
		contactService.updateList(id, dto.getName(), dto.getDescription());
		return ResponseEntity.ok(new Response("List updated successfully"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Response> deleteList(@PathVariable Long id) {
		contactService.deleteList(id);
		return ResponseEntity.ok(new Response("List deleted successfully"));
	}

	@PostMapping("/{id}/contacts")
	public ResponseEntity<Response> addContactsToList(@PathVariable Long id, @RequestBody AddContactsToListDto dto) {
		contactService.addContactsToList(id, dto.getContactIds());
		return ResponseEntity.ok(new Response("Contacts added successfully"));
	}

	@DeleteMapping("/{listId}/contacts/{contactId}")
	public ResponseEntity<Response> removeContactFromList(@PathVariable Long listId, @PathVariable Long contactId) {
		contactService.removeContactFromList(listId, contactId);
		return ResponseEntity.ok(new Response("Contact removed successfully"));
	}

	@GetMapping("/{id}/contacts")
	public ResponseEntity<Map<String, List<ContactListDto>>> getContactListWithContacts(@PathVariable Long id) {
		ContactListDto dto = contactService.getContactListDtoById(id);
		return ResponseEntity.ok(Map.of("contactLists", List.of(dto)));
	}
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllContacts() {
	    Map<String, Object> result = contactService.getAllContactsWithCounts();
	    return ResponseEntity.ok(result);
	}

	@GetMapping("/contactlists")
	public List<ContactListDto> getAllContactLists(@RequestParam(required = false) String search) {
		return contactService.getAllContactLists(search);
	}

	@GetMapping("/contactInfo/{id}")
	public ContactDto getContactById(@PathVariable Long id) {
		return contactService.getContactById(id);
	}
}
