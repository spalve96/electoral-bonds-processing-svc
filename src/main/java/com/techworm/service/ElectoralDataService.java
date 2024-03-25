/**
 * 
 */
package com.techworm.service;

import java.util.List;

import com.techworm.model.DataByParty;
import com.techworm.model.ElectoralDataModel;

/**
 * 
 */
public interface ElectoralDataService {

	List<ElectoralDataModel> getDetailReport();

	/**
	 * @param partyName
	 * @return
	 */
	DataByParty getReportByPartyName(String partyName);

}
