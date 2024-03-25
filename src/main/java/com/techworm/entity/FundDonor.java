/**
 * 
 */
package com.techworm.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FundDonor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String referenceNo;
	private LocalDate journalDate;
	private LocalDate dateOfPurchase;
	private LocalDate dateOfExpiry;
	private String purchaserName;
	private String bondNumber;
	private Double denominationAmount;
	private String bankCode;
	private String issueTeller;
	private String status;

}
