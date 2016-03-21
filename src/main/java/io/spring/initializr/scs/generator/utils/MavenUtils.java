/*
 * Copyright 2016 the original author or authors.
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
package io.spring.initializr.scs.generator.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author hillert
 *
 */
public class MavenUtils {

	public static void addDockerPlugin(InputStream is, OutputStream os) {
		final MavenXpp3Reader reader = new MavenXpp3Reader();

		Model pomModel;
		try {
			pomModel = reader.read(is);
		} catch (IOException | XmlPullParserException e) {
			throw new IllegalStateException(e);
		}

		final Plugin dockerPlugin = new Plugin();
		dockerPlugin.setGroupId("io.fabric8");
		dockerPlugin.setArtifactId("docker-maven-plugin");
		dockerPlugin.setVersion("0.14.2");

		final Xpp3Dom mavenPluginConfiguration = new Xpp3Dom("configuration");


		final Xpp3Dom images = addElement(mavenPluginConfiguration, "images");

		final Xpp3Dom image = addElement(images, "image");
		addElement(image, "name", "${docker.image}");

		final Xpp3Dom build = addElement(image, "build");
		addElement(build, "from", "anapsix/alpine-java:8");

		final Xpp3Dom volumes = addElement(build, "volumes");
		addElement(volumes, "volume", "/tmp");

		final Xpp3Dom entryPoint = new Xpp3Dom("entryPoint");
		build.addChild(entryPoint);

		final Xpp3Dom exec = new Xpp3Dom("exec");
		entryPoint.addChild(exec);

		addElement(exec, "arg", "java");
		addElement(exec, "arg", "-jar");
		addElement(exec, "arg", "/maven/demo.jar");

		final Xpp3Dom assembly = addElement(build, "assembly");
		addElement(assembly, "descriptor", "assembly.xml");

		dockerPlugin.setConfiguration(mavenPluginConfiguration);
		pomModel.getBuild().addPlugin(dockerPlugin);

		pomModel.toString();

		final MavenXpp3Writer writer = new MavenXpp3Writer();
		OutputStreamWriter w;
		try {
			w = new OutputStreamWriter(os, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}

		try {
			writer.write(w, pomModel);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}

	private static Xpp3Dom addElement(Xpp3Dom parentElement, String elementName) {
		return addElement(parentElement, elementName, null);
	}

	private static Xpp3Dom addElement(Xpp3Dom parentElement, String elementName, String elementValue) {
		Xpp3Dom child = new Xpp3Dom(elementName);
		child.setValue(elementValue);
		parentElement.addChild(child);
		return child;
	}
}
