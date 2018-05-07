/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.initializr.scs.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.scs.generator.utils.MavenUtils;

/**
*
* @author Gunnar Hillert
*
*/
public class ScsProjectGenerator extends ProjectGenerator {

	@Override
	public File generateProjectStructure(ProjectRequest request) {

		final File rootDir = super.generateProjectStructure(request);

		boolean mavenBuild = "maven".equals(request.getBuild());

		if (mavenBuild) {
			final File dir = new File(rootDir, request.getBaseDir());

			final File dockerDir = new File(dir, "src/main/docker");
			dockerDir.mkdirs();
			write(new File(dockerDir, "assembly.xml"), "assembly.xml", resolveModel(request));

			final File inputFile = new File(dir, "pom.xml");
			final File tempOutputFile = new File(dir, "pom_tmp.xml");

			try {
				final InputStream is = new FileInputStream(inputFile);
				final OutputStream os = new FileOutputStream(tempOutputFile);
				MavenUtils.addDockerPlugin(is, os);
			} catch (FileNotFoundException e) {
				throw new IllegalStateException(e);
			}

			inputFile.delete();
			tempOutputFile.renameTo(inputFile);
			tempOutputFile.delete();
		}

		return rootDir;

	}
}
