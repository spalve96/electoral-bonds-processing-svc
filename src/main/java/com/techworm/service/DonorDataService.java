/**
 * 
 */
package com.techworm.service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.techworm.model.DonorModel;

/**
 * 
 */
public interface DonorDataService {

	/**
	 * @param file
	 * @param fileType 
	 * @return 
	 */
	List<DonorModel> save(MultipartFile file, String fileType);

	/**
	 * @return
	 */
	public Stream<Path> loadAll();

	/**
	 * @param filename
	 * @return
	 */
	Resource load(String filename);

	/**
	 * 
	 */
	void deleteAll();

}
