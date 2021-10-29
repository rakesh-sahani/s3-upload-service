package com.app.eventrade.s3uploadservice.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.eventrade.s3uploadservice.exception.CustomException;
import com.app.eventrade.s3uploadservice.service.S3Service;
import com.app.eventrade.s3uploadservice.util.AppConstants;


@RestController
@RequestMapping("/s3")
public class S3Controller {
	
	@Autowired
	private S3Service s3Service;

	@PostMapping(value = "/file",consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Object> uploadFile(@RequestParam("bucketName") String bucketName,@RequestParam("access") String access,
			@RequestParam("fileName") String fileName,
			@RequestParam("filePath") String filePath,			
			@RequestPart("file") MultipartFile file) throws CustomException, IOException {	
		if (file.isEmpty()) 
		{
			throw new CustomException(HttpStatus.OK.value(),HttpStatus.BAD_REQUEST.value(),AppConstants.REQUEST_BODY_MISSING);
		}
		return new ResponseEntity<>(s3Service.uploadFile(bucketName,access,fileName,filePath, file), HttpStatus.OK);
	}
	
	 @GetMapping(value= "/file")
		public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam("fileUrl") String fileUrl)
				throws CustomException {
			if (fileUrl.isEmpty()) {
				throw new CustomException(HttpStatus.OK.value(), HttpStatus.BAD_REQUEST.value(),AppConstants.REQUEST_BODY_MISSING);
			}
			String[] file = fileUrl.split("/");
			String bucketName = file[3];
			String filePath = file[4] + "/" + file[5];	
			byte[] data = s3Service.downloadFile(bucketName,filePath);
			ByteArrayResource resource = new ByteArrayResource(data);
			return ResponseEntity.ok().contentLength(data.length).header("Content-type", "application/octet-stream")
					.header("Content-disposition", "attachment; filename=\"" + file[5] + "\"").body(resource);
		}
	
    @DeleteMapping("/file")
    public ResponseEntity<Object> deleteFile(@RequestParam("fileUrl") String fileUrl) throws CustomException {
        if (fileUrl.isEmpty()) 
        {
			throw new CustomException(HttpStatus.OK.value(),HttpStatus.BAD_REQUEST.value(),AppConstants.REQUEST_BODY_MISSING);
		}
        String[] file = fileUrl.split("/");
		String bucketName = file[3];
		String filePath = file[4] + "/" + file[5];	
		return new ResponseEntity<>(s3Service.deleteFile(bucketName,filePath), HttpStatus.OK);
    }
}
