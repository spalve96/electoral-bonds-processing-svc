/**
 * 
 */
package com.techworm.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDataModel {
	private String referenceNo;
	private LocalDate journalDate;
	private LocalDate transactionDate;
	private LocalDate dateOfExpiry;
	private String name;
	private String prefix;
	private String bondNumber;
	private Double denominationAmount;
	private String bankCode;
	private String issueTeller;
	private String status;
	private String accountNoOfPoliticalParty;
}
