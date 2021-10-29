package com.app.eventrade.s3uploadservice.service.Impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.app.eventrade.s3uploadservice.DTO.Response;
import com.app.eventrade.s3uploadservice.exception.CustomException;
import com.app.eventrade.s3uploadservice.service.S3Service;
import com.app.eventrade.s3uploadservice.util.AppConstants;

@Service
public class S3ServiceImpl implements S3Service {

	@Autowired
	private AmazonS3 s3Client;

	private static LocalDateTime timeStamp = LocalDateTime.now();

	@Override
	public Response uploadFile(String bucketName, String access, String fileName, String filePath,
			MultipartFile multipartFile) throws CustomException, IOException {
		String fileUrl = "";
		try {
			File file = convertMultiPartToFile(multipartFile, fileName);
			uploadFileTos3bucket(bucketName, access, filePath, file.getName(), file);
			if (filePath == null) {
				fileUrl = file.getName();
			} else {
				fileUrl = filePath + file.getName();
			}
			file.delete();
		} catch (AmazonServiceException ase) {
			System.out.println("Error Message:    " + ase.getMessage());

		} catch (AmazonClientException ace) {
			System.out.println("Error Message: " + ace.getMessage());
		}
		return new Response(timeStamp, HttpStatus.OK.value(), HttpStatus.OK.value(),
				s3Client.getUrl(bucketName, fileUrl));
	}

	private File convertMultiPartToFile(MultipartFile file, String fileName) throws IOException {
		String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		;
		File convFile = new File(file.getOriginalFilename().replace(file.getOriginalFilename(), fileName + extension));
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private void uploadFileTos3bucket(String bucketName, String access, String filePath, String fileName, File file) {
		PutObjectRequest putObjectRequest;
		if (!s3Client.doesBucketExistV2(bucketName)) {
			s3Client.createBucket(bucketName);
		}
		if (filePath == null) {
			putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
		} else {
			putObjectRequest = new PutObjectRequest(bucketName, filePath + fileName, file);
		}
		if (access.equals(AppConstants.PRIVATE)) {
			s3Client.putObject(putObjectRequest);
		} else if (access.equals(AppConstants.PUBLIC)) {
			s3Client.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));
		}
	}

	@Override
	@Async
	public byte[] downloadFile(String bucketName, String filePath) throws CustomException {
		byte[] content = null;
		S3Object s3Object = s3Client.getObject(bucketName, filePath);
		S3ObjectInputStream stream = s3Object.getObjectContent();
		try {
			content = IOUtils.toByteArray(stream);
			s3Object.close();
		} catch (final IOException ex) {
			throw new CustomException(HttpStatus.OK.value(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
		}
		return content;
	}

	@Override
	public Response deleteFile(String bucketName, String filePath) throws CustomException {
		try {
			s3Client.deleteObject(bucketName, filePath);
		} catch (AmazonClientException ace) {
			System.out.println("Error Message: " + ace.getMessage());
		}
		return new Response(timeStamp, HttpStatus.OK.value(), HttpStatus.OK.value(), AppConstants.FILE_DELETED);
	}
}
