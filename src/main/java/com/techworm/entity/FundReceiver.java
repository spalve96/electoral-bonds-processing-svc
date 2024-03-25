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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundReceiver {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate dateOfEncashment;
	private String nameofThePoliticalParty;
	private String bondNumber;
	private String accountNoOfPoliticalParty;
	private Double denominationAmount;
	private String bankCode;
	private String payTeller;
}
