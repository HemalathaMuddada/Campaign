package com.xtremand.contact.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.xtremand.contact.repository.ContactListRepository;
import com.xtremand.contact.repository.ContactRepository;
import com.xtremand.contact.specification.ContactListSpecification;
import com.xtremand.domain.dto.ContactDto;
import com.xtremand.domain.dto.ContactListDto;
import com.xtremand.domain.entity.Contact;
import com.xtremand.domain.entity.ContactList;

import jakarta.persistence.EntityNotFoundException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContactService {

	private final ContactRepository contactRepository;
	private final ContactListRepository contactListRepository;

	public ContactService(ContactRepository contactRepository, ContactListRepository contactListRepository) {
		this.contactRepository = contactRepository;
		this.contactListRepository = contactListRepository;
	}

	/* ======================= File Upload & Parsing ======================= */

	public void save(MultipartFile file, boolean skipHeader) {
		String filename = Optional.ofNullable(file.getOriginalFilename()).filter(name -> !name.isBlank())
				.orElseThrow(() -> new IllegalArgumentException("Filename is missing."));

		List<Contact> contacts = parseFile(file, filename, skipHeader);
		contactRepository.saveAll(contacts);
	}

	private List<Contact> parseFile(MultipartFile file, String filename, boolean skipHeader) {
		try {
			if (filename.endsWith(".csv")) {
				return parseCsv(file, skipHeader);
			} else if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {
				return parseExcel(file, skipHeader);
			} else {
				throw new IllegalArgumentException("Unsupported file type: " + filename);
			}
		} catch (IOException | CsvException e) {
			throw new RuntimeException("Failed to parse file: " + filename, e);
		}
	}

	private List<Contact> parseCsv(MultipartFile file, boolean skipHeader) throws IOException, CsvException {
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
			List<String[]> rows = reader.readAll();
			return rows.stream().skip(skipHeader ? 1 : 0).map(this::buildContactFromRow).toList();
		}
	}

	private List<Contact> parseExcel(MultipartFile file, boolean skipHeader) throws IOException {
		List<Contact> contacts = new ArrayList<>();
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();
			if (skipHeader && rows.hasNext()) {
				rows.next();
			}
			while (rows.hasNext()) {
				contacts.add(buildContactFromRow(rows.next()));
			}
		}
		return contacts;
	}

	private Contact buildContactFromRow(String[] row) {
		Contact contact = new Contact();
		contact.setFirstName(getSafeValue(row, 0));
		contact.setLastName(getSafeValue(row, 1));
		contact.setEmail(getSafeValue(row, 2));
		contact.setPhone(getSafeValue(row, 3));
		contact.setJobTitle(getSafeValue(row, 4));
		contact.setCompany(getSafeValue(row, 5));
		contact.setLocation(getSafeValue(row, 6));
		contact.setTags(getSafeValue(row, 7));
		return contact;
	}

	private Contact buildContactFromRow(Row row) {
		Contact contact = new Contact();
		contact.setFirstName(getCellValue(row.getCell(0)));
		contact.setLastName(getCellValue(row.getCell(1)));
		contact.setEmail(getCellValue(row.getCell(2)));
		contact.setPhone(getCellValue(row.getCell(3)));
		contact.setJobTitle(getCellValue(row.getCell(4)));
		contact.setCompany(getCellValue(row.getCell(5)));
		contact.setLocation(getCellValue(row.getCell(6)));
		contact.setTags(getCellValue(row.getCell(7)));
		return contact;
	}

	private String getSafeValue(String[] row, int index) {
		return index < row.length ? Optional.ofNullable(row[index]).orElse("") : "";
	}

	private String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		return switch (cell.getCellType()) {
		case STRING -> cell.getStringCellValue();
		case NUMERIC -> String.valueOf(cell.getNumericCellValue());
		case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
		case FORMULA -> evaluateFormula(cell);
		default -> "";
		};
	}

	private String evaluateFormula(Cell cell) {
		try {
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
			return switch (evaluator.evaluateFormulaCell(cell)) {
			case STRING -> cell.getStringCellValue();
			case NUMERIC -> String.valueOf(cell.getNumericCellValue());
			case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
			default -> "";
			};
		} catch (Exception e) {
			return "";
		}
	}

	/* ======================= Contact List Management ======================= */

	public ContactList createList(String name, String description) {
		if (contactListRepository.existsByNameIgnoreCase(name)) {
			throw new IllegalArgumentException("A contact list with the given name already exists");
		}
		ContactList list = new ContactList();
		list.setName(name);
		list.setDescription(description);
		return contactListRepository.save(list);
	}

	public ContactList updateList(Long id, String name, String description) {
		ContactList list = getListById(id);
		if (contactListRepository.existsByNameIgnoreCase(name) && !list.getName().equalsIgnoreCase(name)) {
			throw new IllegalArgumentException("A contact list with the given name already exists");
		}
		list.setName(name);
		list.setDescription(description);
		return contactListRepository.save(list);
	}

	public void deleteList(Long id) {
		contactListRepository.deleteById(id);
	}

	public void addContactsToList(Long listId, List<Long> contactIds) {
		ContactList list = getListById(listId);
		Set<Long> existingContactIds = list.getContacts().stream().map(Contact::getId).collect(Collectors.toSet());

		List<Long> duplicates = contactIds.stream().filter(existingContactIds::contains).collect(Collectors.toList());

		if (!duplicates.isEmpty()) {
			throw new IllegalArgumentException("The following contacts are already in the list: " + duplicates);
		}
		List<Contact> contacts = contactRepository.findAllById(contactIds);
		list.getContacts().addAll(contacts);
		contactListRepository.save(list);
	}

	public void removeContactFromList(Long listId, Long contactId) {
		ContactList list = getListById(listId);
		list.getContacts().removeIf(contact -> contactId.equals(contact.getId()));
		contactListRepository.save(list);
	}

	public List<ContactListDto> getAllContactLists(String search) {
		Specification<ContactList> spec = ContactListSpecification.hasNameOrDescriptionLike(search);
		List<ContactList> lists = contactListRepository.findAll(spec);
		return lists.stream().map(list -> new ContactListDto(list.getId(), list.getName(), list.getDescription(),
				list.getContacts().size())).toList();
	}

	public ContactDto getContactById(Long id) {
		Contact contact = contactRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
		return mapToDto(contact);
	}

	/* ======================= DTO Mapping & Retrieval ======================= */

	public ContactListDto getContactListDtoById(Long listId) {
		ContactList list = getListById(listId);
		List<ContactDto> contactDtos = list.getContacts().stream().map(this::mapToDto).toList();
		return new ContactListDto(list.getId(), list.getName(), list.getDescription(), contactDtos);
	}
	
	public Map<String, Object> getAllContactsWithCounts() {
		List<ContactDto> contacts = getAllContacts();
		long totalContacts = contacts.size();
		long activeContacts = contacts.stream().filter(ContactDto::isActive).count();

		Map<String, Object> response = new HashMap<>();
		response.put("totalContacts", totalContacts);
		response.put("activeContacts", activeContacts);
		response.put("contacts", contacts);

		return response;
	}


	public List<ContactDto> getAllContacts() {
		return contactRepository.findAll().stream().map(this::mapToDto).toList();
	}

	private ContactList getListById(Long id) {
		return contactListRepository.findById(id).orElseThrow(() -> new RuntimeException("List not found"));
	}

	private ContactDto mapToDto(Contact contact) {
		ContactDto dto = new ContactDto();
		dto.setId(contact.getId());
		dto.setFirstName(contact.getFirstName());
		dto.setLastName(contact.getLastName());
		dto.setEmail(contact.getEmail());
		dto.setPhone(contact.getPhone());
		dto.setJobTitle(contact.getJobTitle());
		dto.setCompany(contact.getCompany());
		dto.setLocation(contact.getLocation());
		dto.setTags(contact.getTags());
		dto.setCreatedAt(contact.getCreatedAt());
		dto.setUpdatedAt(contact.getUpdatedAt());
		dto.setCreatedBy(contact.getCreatedBy());
		dto.setUpdatedBy(contact.getUpdatedBy());
		dto.setActive(contact.isActive());
		return dto;
	}
}
