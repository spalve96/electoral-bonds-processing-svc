/**
 * 
 */
package com.techworm.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectoralDataModel {
	private String purchaserName;
	private Double denominationAmount;
	private String bondNumber;
	private LocalDate dateOfPurchase;
	ReceiverPartyModel party;
}
