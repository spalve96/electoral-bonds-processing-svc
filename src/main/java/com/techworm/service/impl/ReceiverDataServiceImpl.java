/**
 * 
 */
package com.techworm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techworm.entity.FundReceiver;
import com.techworm.model.FileDataModel;
import com.techworm.model.ReceiverPartyModel;
import com.techworm.repository.ReceiverRepository;
import com.techworm.service.ReceiverDataService;
import com.techworm.utilis.FileProcessingUtility;
import com.techworm.utilis.PdfReaderUtility;

import lombok.extern.log4j.Log4j2;

/**
 * 
 */
@Log4j2
@Service
public class ReceiverDataServiceImpl implements ReceiverDataService {

	@Autowired
	FileProcessingUtility fileProcessingUtility;
	@Autowired
	PdfReaderUtility pdfReaderUtility;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	ReceiverRepository receiverRepository;

	@Override
	public List<ReceiverPartyModel> save(MultipartFile file, String fileType) {
		List<FileDataModel> data = new ArrayList<FileDataModel>();
		try {
//			fileProcessingUtility.copyFile(file);
			if (file.getOriginalFilename().endsWith(".xlsx")) {
				data = fileProcessingUtility.readFile(file);
			} else if (file.getOriginalFilename().endsWith(".pdf")) {
				data = pdfReaderUtility.readPdf(file, fileType);
			}
			log.info("Converted data size (FROM PDF)" + data.size());
//			List<FundDonor> fianlList = mapper.convertValue(data, new TypeReference<List<FundDonor>>() {});
			List<FundReceiver> fianlList = data.stream()
					.map(fileDataModel -> FundReceiver.builder()
							.accountNoOfPoliticalParty(fileDataModel.getAccountNoOfPoliticalParty())
							.bankCode(fileDataModel.getBankCode()).bondNumber(fileDataModel.getBondNumber())
							.dateOfEncashment(fileDataModel.getTransactionDate())
							.denominationAmount(fileDataModel.getDenominationAmount())
							.nameofThePoliticalParty(fileDataModel.getName()).payTeller(fileDataModel.getIssueTeller())
							.build())
					.collect(Collectors.toList());
			log.info("Converted data size (POST MAPPING)" + data.size());
			List<FundReceiver> savedData = receiverRepository.saveAll(fianlList);
			log.info("Saved  data size (POST SAVE)" + data.size());
			return mapper.convertValue(savedData, new TypeReference<List<ReceiverPartyModel>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error:" + e.getMessage());
		}
	}
}
