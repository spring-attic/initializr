/*
 * Copyright 2016-2017 the original author or authors.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.scs.generator.ScsProjectGenerator;
import io.spring.initializr.util.Version;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author Gunnar Hillert
 *
 */
@SpringBootApplication
public class InitializrScsApplication {

	private static final Version BOOT_2_1_X_VERSION = Version.parse("2.1.0.RELEASE");

	public static void main(String[] args) {
		SpringApplication.run(InitializrScsApplication.class, args);
	}

	@Bean
	public ProjectGenerator projectGenerator() {
		return new ScsProjectGenerator();
	}

	@Bean
	public InitializrMetadataProvider initializrMetadataProvider(
			InitializrProperties properties,
			ObjectMapper objectMapper,
			RestTemplateBuilder restTemplateBuilder) {
		InitializrMetadata metadata = InitializrMetadataBuilder
				.fromInitializrProperties(properties).build();
		return new CustomInitializrMetadataProvider(metadata,
				objectMapper, restTemplateBuilder.build());
	}

	@Bean
	public ProjectRequestPostProcessor webActuatorProjectRequestPostProcessor() {
		return new ProjectRequestPostProcessor() {

			@Override
			public void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
				// Inject the required by SCDF spring-boot-starter-web and spring-boot-starter-actuator dependencies
				Version bootVersion = Version.parse(request.getBootVersion());
				if (request.getResolvedDependencies() != null && bootVersion.compareTo(BOOT_2_1_X_VERSION) >= 0) {

					Dependency starterWeb = new Dependency();
					starterWeb.setGroupId("org.springframework.boot");
					starterWeb.setArtifactId("spring-boot-starter-web");
					request.getResolvedDependencies().add(starterWeb);

					Dependency starterActuator = new Dependency();
					starterActuator.setGroupId("org.springframework.boot");
					starterActuator.setArtifactId("spring-boot-starter-actuator");
					request.getResolvedDependencies().add(starterActuator);
				}
			}
		};
	}
}
