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

import io.swagger.annotations.ApiOperation;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BootIsoCodeController {

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/getIsoCode", produces = "text/plain")
	@ApiOperation("Returns the IsoCode for the country ")
	public String getIsoCode(@RequestParam("country") String country) {
		String returnVal = "notfound";

		String normalizedCountry = IsoCode.normalize(country);
		System.out.println("Checking for country " + country + " normalized "
				+ normalizedCountry);
		IsoCode iso = IsoCode.valueOf(normalizedCountry);
		if (iso != null) {
			returnVal = iso.getIsoCode();
		}
		// Demonstrating load balancing capability by displaying hostname
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");

		return returnVal + " Deployment Server=" + hostname;

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/getAllIsoCodes", produces = "text/plain")
	@ApiOperation("Returns All the available IsoCodes ")
	public List<IsoCode> getAllIsoCodes() {

		List<IsoCode> returnVal = Arrays.asList(IsoCode.values());

		return returnVal;

	}

	@RequestMapping(method = RequestMethod.GET, value = "/health")
	@ApiOperation("Used to verify the health of the service")
	public String health() {
		return "IsoCodeResource is ok";
	}
}
