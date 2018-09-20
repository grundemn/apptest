package com.grainger.openshift.isocode;

import org.springframework.data.repository.CrudRepository;

public interface IsoCodeRepository extends
		CrudRepository<IsoCodeEntity, Integer> {

	IsoCodeEntity findByCountryName(String country);

	IsoCodeEntity findByIsoCode(String isoCode);

}
