package com.app.eventrade.s3uploadservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
public class S3UploadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(S3UploadServiceApplication.class, args);
	}

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.OAS_30)
				.apiInfo(new ApiInfoBuilder().title("S3 Upload Service API")
						.description("EvenTrade - S3 Upload Service API").version("0.0.1-SNAPSHOT")
						.license("EvenTrade").licenseUrl("https://www.eventrade.in")
						.contact(new Contact("Rakesh Sahani", "", "rakeshsahani@live.in")).build()).select()
				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).build();
	}

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
	    return new RestTemplate();
	}
}
