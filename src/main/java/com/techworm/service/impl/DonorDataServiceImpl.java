/**
 * 
 */
package com.techworm.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techworm.entity.FundDonor;
import com.techworm.entity.FundReceiver;
import com.techworm.model.FileDataModel;
import com.techworm.model.DonorModel;
import com.techworm.repository.DonorRepository;
import com.techworm.service.DonorDataService;
import com.techworm.utilis.FileProcessingUtility;
import com.techworm.utilis.PdfReaderUtility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * 
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class DonorDataServiceImpl implements DonorDataService {
	private final DonorRepository donorRepository;

	@Value("${com.techworm.application.from}")
	private String filePath;

	@Value("${com.techworm.application.from}")
	private Path uploadPath;
	@Autowired
	FileProcessingUtility fileProcessingUtility;
	@Autowired
	PdfReaderUtility pdfReaderUtility;
	@Autowired
	ObjectMapper mapper;
	@Setter
	@Getter
	List<FundReceiver> fianlList = new ArrayList<FundReceiver>();

	@Override
	public List<DonorModel> save(MultipartFile file, String fileType) {
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
			List<FundDonor> fianlList = data.stream()
					.map(fileDataModel -> FundDonor.builder().bankCode(fileDataModel.getBankCode())
							.bondNumber(fileDataModel.getBondNumber()).dateOfExpiry(fileDataModel.getDateOfExpiry())
							.dateOfPurchase(fileDataModel.getTransactionDate())
							.denominationAmount(fileDataModel.getDenominationAmount())
							.issueTeller(fileDataModel.getIssueTeller()).journalDate(fileDataModel.getJournalDate())
							.purchaserName(fileDataModel.getName()).referenceNo(fileDataModel.getReferenceNo())
							.status(fileDataModel.getStatus()).build())
					.collect(Collectors.toList());
			log.info("Converted data size (POST MAPPING)" + data.size());
			List<FundDonor> savedData = donorRepository.saveAll(fianlList);
			log.info("Saved  data size (POST SAVE)" + data.size());
			return mapper.convertValue(savedData, new TypeReference<List<DonorModel>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error:" + e.getMessage());
		}
	}

	@Override
	public Resource load(String fileName) {
		Path filePath = uploadPath.resolve(fileName);
		try {
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		if (uploadPath != null) {
			FileSystemUtils.deleteRecursively(uploadPath.toFile());
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.uploadPath, 1).filter(path -> !path.equals(this.uploadPath))
					.map(this.uploadPath::relativize);
		} catch (IOException e) {
			throw new RuntimeException("Could not load the files!");
		}
	}
}
