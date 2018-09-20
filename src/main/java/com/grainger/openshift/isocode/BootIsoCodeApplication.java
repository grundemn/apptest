/**
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grainger.openshift.isocode;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.fasterxml.classmate.TypeResolver;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackageClasses = { BootIsoCodeController.class })
public class BootIsoCodeApplication {

	private static final Logger LOG = LoggerFactory
			.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(BootIsoCodeApplication.class, args);
	}

	/**
	 * 
	 * Set up the data for the service
	 * 
	 * @param repository
	 * @return
	 */
	@Bean
	public CommandLineRunner loadData(IsoCodeRepository repository) {
		return (args) -> {
			boolean isInitialized = false;
			// save a the iso code enum to the db if not present
			try {

				Iterable<IsoCodeEntity> result = repository.findAll();
				isInitialized = result.iterator().hasNext();
			} catch (Exception e) {

			}
			if (!isInitialized) {
				for (IsoCode isoCode : IsoCode.values()) {
					repository.save(new IsoCodeEntity(isoCode.name(), isoCode
							.getIsoCode()));
				}
			}

			// fetch all customers
			LOG.info("Isocodes found with findAll():");
			LOG.info("-------------------------------");
			for (IsoCodeEntity iso : repository.findAll()) {
				LOG.info(iso.toString());
			}
			LOG.info("");

		};
	}

	@Bean
	public Docket bootApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any())
				.build().pathMapping("/");

	}

	@Autowired
	private TypeResolver typeResolver;

}