/**
 * 
 */
package com.techworm.utilis;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.techworm.constants.AppConstants;
import com.techworm.model.FileDataModel;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@SuppressWarnings("unused")
public class PdfReaderUtility {

	public List<FileDataModel> readPdf(MultipartFile file, String fileType) {
		List<FileDataModel> dataList = new ArrayList<>();
		try {
//            File pdfFile = new File("sample.pdf"); //Read from local drive 
//        	PDDocument document = PDDocument.load(pdfFile);
			PDDocument document = PDDocument.load(file.getInputStream());
			PDFTextStripper stripper = new PDFTextStripper();
			String extractedText = stripper.getText(document);
//			Replace keys whiteSpaces
//			extractedText = extractedText.replace("Date of Purchase Purchaser Name Denomination",
//					"DateofPurchase PurchaserName Denomination");
			convertTextToJson(extractedText, dataList, fileType);
			document.close();
		} catch (IOException | JSONException e) {
			log.error("Error while reading PDF ", e);
		}
		log.info("Processed data size : " + dataList.size());
		return dataList;
	}

	private LocalDate convertToLocalDate(String inputDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);
		try {
			if (ObjectUtils.isNotEmpty(inputDate))
				return LocalDate.parse(inputDate, formatter);
		} catch (DateTimeParseException e) {
			log.error("Error parsing date: " + e.getMessage());
		}
		return null;

	}

	private LocalDate convertToLocalDateHypen(String inputDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
		try {
			if (ObjectUtils.isNotEmpty(inputDate)) {
				if (inputDate.length() == 10)
					inputDate = "0".concat(inputDate);
				return LocalDate.parse(inputDate, formatter);
			}
		} catch (DateTimeParseException e) {
			log.error("Error parsing date: " + e.getMessage());
		}
		return null;

	}

	public List<FileDataModel> convertTextToJson(String text, List<FileDataModel> dataList, String fileType)
			throws JSONException {
		String[] lines = text.split("\n");
		log.info("Total records in PDF : " + lines.length);
//		splitterAndTester(text);
		try {
			for (int i = 1; i < lines.length; i++) {
				String recordLine = lines[i];
				if (StringUtils.isNotBlank(recordLine)) {
					if (StringUtils.isNotBlank(recordLine) && (!recordLine.contains(AppConstants.DENOMINATIONS)
							&& (!recordLine.contains(AppConstants.ACCOUNT)) && (!recordLine.contains(AppConstants.PAGE))
							&& (!recordLine.contains(AppConstants.TELLER)) && (!recordLine.contains(AppConstants.CODE))
							&& (!recordLine.contains(AppConstants.BRANCH))
							&& (!recordLine.contains(AppConstants.PREFIX))
							&& (!recordLine.contains(AppConstants.ENCASHMENT))
							&& (!recordLine.contains(AppConstants.SR_NO)))
							&& (!recordLine.contains(AppConstants.NUMBER)) && (!recordLine.contains(AppConstants.DATE))
							&& (!recordLine.contains(AppConstants.BOND))
							&& (!recordLine.contains(AppConstants.PURCHASE))
							&& (!recordLine.contains(AppConstants.POLITICAL))) {
						if (recordLine.substring(0, recordLine.length() - 30).contains(",")) {
							recordLine = recordLine.replaceFirst(",", "");
							log.info("recordLine after replace: " + recordLine);
						}
						if (StringUtils.isNotBlank(fileType) && fileType.equals(AppConstants.FILE_TYPE_RECEIVER_FILE)) {
							int srNoLength = recordLine.substring(0, recordLine.indexOf("/") - 3).strip().length();
							log.info("Receivers file record number : ".concat(recordLine.substring(0, srNoLength)));
							log.info("recordLine before replace: " + recordLine);
							getDataFromReceiversFile(dataList, recordLine);
						} else if (StringUtils.isNotBlank(fileType)
								&& fileType.equals(AppConstants.FILE_TYPE_DONOR_FILE)) {
//							int srNoLength = recordLine.substring(0, recordLine.indexOf("-") - 3).strip().length();
//							log.info("Donors file record number : ".concat(recordLine.substring(0, srNoLength)));
							log.info("recordLine before replace: " + recordLine);
							getDataFromDonorsFile(dataList, recordLine);
						} else
							log.info(String.format("Invalid file type : %s, file cannot be processed.", fileType));
					}
				}

			}
		} catch (Exception e) {
			log.error("Exception while processing/reading PDF file.", e);
		}
		log.info("Processed data size : " + dataList.size());
		return dataList;
	}

	/**
	 * @param dataList
	 * @param recordLine
	 */
	private void getDataFromReceiversFile(List<FileDataModel> dataList, String recordLine) {
		int indexOfFirstSlashForSr = recordLine.indexOf("/") - 3;
		int srNoLength = recordLine.substring(0, indexOfFirstSlashForSr).strip().length();
		int indexOfFirstSlash = recordLine.lastIndexOf("/") + 5;
		int indexOfFirstStar = recordLine.indexOf("*");
		int indexOfFirstComma = recordLine.indexOf(",") - 1;
		int indexOfLastStar = recordLine.lastIndexOf("*") + 5;
		String transactionDate = recordLine.substring(srNoLength, srNoLength + 12).strip().trim();
		String name = recordLine.substring(indexOfFirstSlash, indexOfFirstStar - 1).strip().trim();
		String prefix = recordLine.substring(indexOfLastStar, indexOfLastStar + 3).strip().trim();
		String accountNo = recordLine.substring(indexOfFirstStar, indexOfLastStar).strip().trim();
		String bondNumber = recordLine.substring(indexOfLastStar + 3, indexOfFirstComma - 1).strip().trim();
		String denominationAmount = null;
		String tester = recordLine.substring(indexOfFirstComma - 1, (indexOfFirstComma + 11)).strip().trim();
		String finalAmount = tester.substring(0, tester.lastIndexOf(",") + 4);
		String replacedTester = tester.replace(",", "");
		String bankCode = AppConstants.NA;
		if (tester.length() == 11 && !replacedTester.contains(" ")) {
			denominationAmount = tester;
			bankCode = recordLine.substring(indexOfFirstComma + 12, indexOfFirstComma + 18).strip().trim();
		} else if (finalAmount.length() <= 6) {
			denominationAmount = finalAmount;
			bankCode = tester.substring(tester.lastIndexOf(",") + 4, tester.length()).strip().trim();
		} else {
			denominationAmount = recordLine.substring(indexOfFirstComma - 1, (indexOfFirstComma + 9)).strip().trim();
			bankCode = recordLine.substring(indexOfFirstComma + 9, indexOfFirstComma + 15).strip().trim();

		}
		String teller = recordLine.substring(indexOfFirstStar + 37, recordLine.length()).strip().trim();
//						String[] values = putValuesInarray(referenceNo, journalDate, transactionDate, dateOfExpiry,
//								name, prefix, bondNumber, denominationAmount, bankCode, issueTeller, status);
		String denomination = ObjectUtils.isNotEmpty(denominationAmount) ? denominationAmount.replace(",", "") : null;
		dataList.add(FileDataModel.builder().accountNoOfPoliticalParty(accountNo).bankCode(bankCode)
				.bondNumber(prefix.concat(bondNumber).replace(" ", "")).dateOfExpiry(null)
				.denominationAmount(Double.parseDouble(denomination)).issueTeller(teller).journalDate(null).name(name)
				.prefix(prefix).referenceNo(AppConstants.NA).status(AppConstants.NA)
				.transactionDate(convertToLocalDate(ObjectUtils.isNotEmpty(transactionDate) ? transactionDate : null))
				.build());

	}

	/**
	 * @param dataList
	 * @param recordLine
	 */
	private void getDataFromDonorsFile(List<FileDataModel> dataList, String recordLine) {
//		int indexOfFirstHypen = recordLine.indexOf("-");
//		int totalWhitespace = 0;
//		int srNoLength = recordLine.substring(0, indexOfFirstHypen - 3).strip().trim().length();
		String referenceNo = "";
		String journalDate = recordLine.substring(0, recordLine.indexOf("~") - 1).strip().trim();
		String dateOfPurchase = recordLine.substring(recordLine.indexOf("~") + 1, recordLine.lastIndexOf("~")).strip()
				.trim();
		String dateOfExpiry = recordLine.substring(recordLine.lastIndexOf("~") + 1, recordLine.indexOf("|")).strip()
				.trim();
		String name = recordLine.substring(recordLine.indexOf("|") + 1, recordLine.lastIndexOf("|")).strip().trim();
		String bondNumber = recordLine.substring(recordLine.lastIndexOf("|") + 1, recordLine.indexOf("#")).strip()
				.trim();
		String prefix = bondNumber.substring(0, 2);
		String denomination = recordLine.substring(recordLine.lastIndexOf("#") + 1, recordLine.indexOf("*")).strip()
				.trim();
		String issueTeller = recordLine.substring(recordLine.lastIndexOf("*") + 1, recordLine.indexOf("$")).strip()
				.trim();
		String status = recordLine.substring(recordLine.indexOf("$") + 1, recordLine.length()).strip().trim();
		String bankCode = issueTeller;

//						String[] values = putValuesInarray(referenceNo, journalDate, transactionDate, dateOfExpiry,
//								name, prefix, bondNumber, denominationAmount, bankCode, issueTeller, status);
		referenceNo = AppConstants.NA;
		dataList.add(FileDataModel.builder().accountNoOfPoliticalParty(AppConstants.NA).bankCode(bankCode)
				.bondNumber(bondNumber)
				.dateOfExpiry(convertToLocalDateHypen(ObjectUtils.isNotEmpty(dateOfExpiry) ? dateOfExpiry : null))
				.denominationAmount(Double.parseDouble(denomination)).issueTeller(issueTeller)
				.journalDate(convertToLocalDateHypen(ObjectUtils.isNotEmpty(dateOfPurchase) ? dateOfPurchase : null))
				.name(name).prefix(prefix).referenceNo(referenceNo).status(status)
				.transactionDate(convertToLocalDateHypen(ObjectUtils.isNotEmpty(journalDate) ? journalDate : null))
				.build());
	}

	private String[] putValuesInarray(String referenceNo, String journalDate, String transactionDate,
			String dateOfExpiry, String name, String prefix, String bondNumber, String denominationAmount,
			String bankCode, String issueTeller, String status) {
		String[] values = new String[11];
		values[0] = referenceNo;
		values[1] = journalDate;
		values[2] = transactionDate;
		values[3] = dateOfExpiry;
		values[4] = name;
		values[5] = prefix;
		values[6] = bondNumber;
		values[7] = denominationAmount;
		values[8] = bankCode;
		values[9] = issueTeller;
		values[10] = status;
		return values;
	}

	private void splitterAndTester(String text) {
		List<String> lines1 = Arrays.asList(text.split("\n")); // Splits based on new recordLine
//		String[] values = lines[i].split("\\s+"); //Splits based on white spaces in string
		int previous = 0;
		for (String recordLine : lines1) {
			if (StringUtils.isNotBlank(recordLine)) {
				int spaceCount = (int) recordLine.chars().filter(c -> c == ' ').count();
				if (spaceCount > previous) {
					System.out.println("Spaces in recordLine: " + spaceCount);
					System.out.println("recordLine length: " + recordLine.length());
					System.out.println("recordLine: " + recordLine);
					previous = spaceCount;
				}
			}
		}
	}
}