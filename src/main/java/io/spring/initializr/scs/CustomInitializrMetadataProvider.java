/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.scs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider;

import org.springframework.web.client.RestTemplate;

public class CustomInitializrMetadataProvider extends DefaultInitializrMetadataProvider {
	public CustomInitializrMetadataProvider(InitializrMetadata metadata, ObjectMapper objectMapper, RestTemplate restTemplate) {
		super(metadata, objectMapper, restTemplate);
	}

	//Overriding this method to filter out Boot versions at or above 2.4.0 (with the new version format)
	@Override
	protected void updateInitializrMetadata(InitializrMetadata metadata) {
		List<DefaultMetadataElement> bootVersions = fetchBootVersions();
		List<DefaultMetadataElement> filteredBootVersions = new ArrayList<>();

		for (DefaultMetadataElement bootVersion : bootVersions) {
			if ( bootVersion.getId().compareTo("2.4.0") < 0 ) {
				filteredBootVersions.add(bootVersion);
			}
		}

		if (filteredBootVersions != null && !filteredBootVersions.isEmpty()) {
			if (filteredBootVersions.stream().noneMatch(DefaultMetadataElement::isDefault)) {
				// No default specified
				filteredBootVersions.get(0).setDefault(true);
			}
			metadata.updateSpringBootVersions(filteredBootVersions);
		}
	}

}
