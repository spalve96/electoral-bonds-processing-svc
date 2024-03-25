/**
 * 
 */
package com.techworm.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.techworm.constants.AppConstants;
import com.techworm.model.FileInfo;
import com.techworm.model.DonorModel;
import com.techworm.service.DonorDataService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 
 */
@Log4j2
@RestController
@Validated
@RequestMapping("excel-manager")
@Tag(name = "Electoral Bonds", description = "Electoral bonds processing service.")
@RequiredArgsConstructor
public class ExcelFileController {
	private final DonorDataService donorDataService;

	@PostMapping("/upload")
	public ResponseEntity<List<DonorModel>> uploadFile(@RequestParam("file") MultipartFile file) {
		String message = "";
		try {
			log.info("File upload request received." + file.getName());
			List<DonorModel> savedData = donorDataService.save(file, AppConstants.FILE_TYPE_DONOR_FILE);
			message = "Uploaded the file successfully: " + file.getOriginalFilename();
			log.info(message);
			return ResponseEntity.status(HttpStatus.OK).body(savedData);
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			log.error(message, e);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(Collections.emptyList());
		}
	}

	@GetMapping("/files")
	public ResponseEntity<List<FileInfo>> getListFiles() {
		List<FileInfo> fileInfos = donorDataService.loadAll().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(ExcelFileController.class, "getFile", path.getFileName().toString()).build().toString();
			return new FileInfo(filename, url);
		}).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = donorDataService.load(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

}
