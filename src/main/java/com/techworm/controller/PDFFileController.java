/**
 * 
 */
package com.techworm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techworm.constants.AppConstants;
import com.techworm.service.DonorDataService;
import com.techworm.service.ReceiverDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("pdf-manager")
@RequiredArgsConstructor
public class PDFFileController {
	private final DonorDataService donorDataService;
	private final ReceiverDataService receiverDataService;

	@PostMapping("/upload-pdf/donor")
	public ResponseEntity<String> uploadDonorPdf(@RequestParam("file") MultipartFile file) {
		try {
			// Validate if the uploaded file is a PDF
			if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
				return ResponseEntity.badRequest().body("Only PDF files are allowed.");
			}
			donorDataService.save(file, AppConstants.FILE_TYPE_DONOR_FILE);
			// You can handle the extracted text as needed
			return ResponseEntity.ok("PDF uploaded successfully. Extracted text:\n");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing the PDF: " + e.getMessage());
		}
	}

	@PostMapping("/upload-pdf/receiver")
	public ResponseEntity<String> uploadReceiverPdf(@RequestParam("file") MultipartFile file) {
		try {
			// Validate if the uploaded file is a PDF
			if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
				return ResponseEntity.badRequest().body("Only PDF files are allowed.");
			}
			receiverDataService.save(file, AppConstants.FILE_TYPE_RECEIVER_FILE);
			// You can handle the extracted text as needed
			return ResponseEntity.ok("PDF uploaded successfully. Extracted text:\n");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing the PDF: " + e.getMessage());
		}
	}

}
