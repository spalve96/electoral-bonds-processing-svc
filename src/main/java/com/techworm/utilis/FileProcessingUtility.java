/**
 * 
 */
package com.techworm.utilis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.techworm.model.FileDataModel;

import lombok.extern.log4j.Log4j2;

/**
 * 
 */
@Component
@Log4j2
public class FileProcessingUtility {
	@Value("#{'${techworm.filePermissions:1,2,3}'.split(',')}")
	private List<String> filePermissions;

	@Value("${com.techworm.application.from}")
	private String filePath;

	private Path uploadPath;
	private int fileCounter = 0;
	@Value("${com.techworm.application.to}")
	private String toLoc;

	/**
	 * Method will create folder with 775 access
	 */
	public void createDirectory(String envPath) throws IOException {
		if (!(Paths.get(envPath)).toFile().exists()) {
			File directory = new File(envPath);
			String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if (os.startsWith("win")) {
				directory.mkdirs();
			} else {
				directory.mkdirs();
				Set<PosixFilePermission> perms = Files.readAttributes(directory.toPath(), PosixFileAttributes.class)
						.permissions();
				if (!filePermissions.isEmpty()) {
					filePermissions.forEach(permission -> {
						perms.add(PosixFilePermission.valueOf(permission));
					});
				}
				Files.setPosixFilePermissions(directory.toPath(), perms);
			}
		}
	}

	public void copyFile(MultipartFile file) throws IOException {
		createDirectory(filePath);
		fileCounter++;
		try {
			String fileNameWithoutExt = file.getOriginalFilename()
					.substring(0, file.getOriginalFilename().lastIndexOf(".")).concat(String.valueOf(fileCounter));
			String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."),
					file.getOriginalFilename().lastIndexOf(".") + 5);
			uploadPath = Paths.get(filePath);
			Files.copy(file.getInputStream(), this.uploadPath.resolve(fileNameWithoutExt.concat(ext)));
		} catch (Exception e) {
			log.error("Error while copying file: " + e);
		}
	}

	public void archiveExistingFiles() {
		log.info("Source and destination locations" + filePath + toLoc);
		try {
			createDirectory(toLoc);
		} catch (IOException e) {
			log.info(toLoc + " : Unable to create Directory");
		}
		Path source = Paths.get(filePath);
		Path target = Paths.get(toLoc);
		try {
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			log.info(" File moved successfully");
		} catch (IOException e) {
			log.info(" File failed to move");
		}
	}

	/**
	 * @param file
	 */
	public List<FileDataModel> readFile(MultipartFile file) {
		List<FileDataModel> dataSet = new ArrayList<FileDataModel>();
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.rowIterator();
			int firstRowNum = sheet.getFirstRowNum();
			XSSFRow headerRow = sheet.getRow(firstRowNum);
//			String purchaseDateHeader = headerRow.getCell(0).getStringCellValue();
//			String donarNameHeader = headerRow.getCell(1).getStringCellValue();
//			String donationAmountHeader = headerRow.getCell(2).getStringCellValue();
			while (rowIterator.hasNext()) {
				Row currentRow = rowIterator.next();
				if (headerRow == currentRow) {
					continue;
				}
				String denomination = ObjectUtils.isNotEmpty(currentRow.getCell(2))
						? currentRow.getCell(2).getStringCellValue().replace(",", "")
						: null;
				dataSet.add(FileDataModel.builder().bondNumber("NOTYETRECEIVED")
						.transactionDate(convertToLocalDate(ObjectUtils.isNotEmpty(currentRow.getCell(0))
								? currentRow.getCell(0).getStringCellValue()
								: null))
						.name(ObjectUtils.isNotEmpty(currentRow.getCell(1)) ? currentRow.getCell(1).getStringCellValue()
								: null)
						.denominationAmount(
								NumberUtils.isDigits(denomination) ? Double.parseDouble(denomination) : null)
						.build());
			}
		} catch (Exception e) {
			log.error("Error while reading file", e);
		}
		return dataSet;

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
}
