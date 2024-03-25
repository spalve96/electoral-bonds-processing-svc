/**
 * 
 */
package com.techworm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techworm.model.DataByParty;
import com.techworm.model.ElectoralDataModel;
import com.techworm.service.ElectoralDataService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * @author spalve
 */
@Log4j2
@RestController
@Validated
@RequestMapping("electoral-data")
@Tag(name = "Electoral Bonds Data", description = "Service to get electoral bonds data.")
@RequiredArgsConstructor
public class ElectoralDataController {
	private final ElectoralDataService electoralDataService;

	@GetMapping("/detailed-report")
	public ResponseEntity<List<ElectoralDataModel>> detailedReport() {
		log.info("Request to get detail report");
		return ResponseEntity.status(HttpStatus.OK).body(electoralDataService.getDetailReport());
	}
	@GetMapping("/report-by-party-name")
	public ResponseEntity<DataByParty> getReportByPartyName(@RequestParam String partyName) {
		return ResponseEntity.status(HttpStatus.OK).body(electoralDataService.getReportByPartyName(partyName));
	}
}
