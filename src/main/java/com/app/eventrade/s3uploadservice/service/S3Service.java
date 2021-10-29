package com.app.eventrade.s3uploadservice.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.app.eventrade.s3uploadservice.DTO.Response;
import com.app.eventrade.s3uploadservice.exception.CustomException;


public interface S3Service {
	
	public Response uploadFile(String bucketName,String access, String fileName, String filePath, MultipartFile multipartFile) throws CustomException, IOException;
	public byte[] downloadFile(String bucketName, String filePath) throws CustomException; 
	public Response deleteFile(String bucketName, String filePath) throws CustomException;
}
