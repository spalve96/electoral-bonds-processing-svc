/**
 * 
 */
package com.techworm.model;

import java.util.ArrayList;
import java.util.List;

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
public class DataByParty {
	private String nameofThePoliticalParty;
	private Double totalReceivedAmount;
	@Builder.Default
	List<DonorModel> donors = new ArrayList<DonorModel>();
}
