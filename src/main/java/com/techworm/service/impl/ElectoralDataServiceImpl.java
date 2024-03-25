/**
 * 
 */
package com.techworm.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techworm.model.DataByParty;
import com.techworm.model.DonorModel;
import com.techworm.model.ElectoralDataModel;
import com.techworm.model.ReceiverPartyModel;
import com.techworm.repository.DonorRepository;
import com.techworm.repository.ReceiverRepository;
import com.techworm.service.ElectoralDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ElectoralDataServiceImpl implements ElectoralDataService {
	private final DonorRepository donorRepository;
	private final ReceiverRepository receiverRepository;
	@Autowired
	ObjectMapper mapper;

	@Override
	public List<ElectoralDataModel> getDetailReport() {
		List<DonorModel> allFundDonors = mapper.convertValue(donorRepository.findAll().stream()
				.filter(a -> a.getStatus().equalsIgnoreCase("Paid")).collect(Collectors.toList()),
				new TypeReference<List<DonorModel>>() {
				});
		List<ReceiverPartyModel> allFundReceivers = mapper.convertValue(receiverRepository.findAll(),
				new TypeReference<List<ReceiverPartyModel>>() {
				});
		Map<String, ReceiverPartyModel> fundReceiverMap = allFundReceivers.stream()
				.collect(Collectors.toMap(ReceiverPartyModel::getBondNumber, Function.identity()));

		List<ElectoralDataModel> eData = allFundDonors.stream().map(o -> {
			return ElectoralDataModel.builder().bondNumber(o.getBondNumber()).dateOfPurchase(o.getDateOfPurchase())
					.denominationAmount(o.getDenominationAmount()).purchaserName(o.getPurchaserName())
					.party(fundReceiverMap.getOrDefault(o.getBondNumber(), null)).build();
		}).collect(Collectors.toList());
		log.info("Total data in report is : " + eData.size());

		return eData;
	}

	@Override
	public DataByParty getReportByPartyName(String partyName) {
		List<ElectoralDataModel> detailReport = this.getDetailReport();
		DataByParty dataByParty = detailReport.stream()
				.filter(o -> o.getParty().getNameofThePoliticalParty().equalsIgnoreCase(partyName))
				.map(o1 -> DataByParty.builder().nameofThePoliticalParty(partyName).donors(detailReport.stream()
						.filter(o -> o.getParty().getNameofThePoliticalParty().equalsIgnoreCase(partyName))
						.map(b -> DonorModel.builder().bondNumber(b.getBondNumber())
								.dateOfPurchase(b.getDateOfPurchase()).denominationAmount(b.getDenominationAmount())
								.purchaserName(b.getPurchaserName()).build())
						.collect(Collectors.toList())).build())
				.findFirst().get();
		Double totalAmount = dataByParty.getDonors().stream().mapToDouble(DonorModel::getDenominationAmount).sum();
		dataByParty.setTotalReceivedAmount(totalAmount);
		return dataByParty;
	}

}
