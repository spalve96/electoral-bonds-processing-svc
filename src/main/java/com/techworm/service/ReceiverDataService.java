/**
 * 
 */
package com.techworm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.techworm.model.ReceiverPartyModel;

/**
 * 
 */
public interface ReceiverDataService {
	/**
	 * @param file
	 * @param fileType
	 * @return
	 */
	List<ReceiverPartyModel> save(MultipartFile file, String fileType);
}
