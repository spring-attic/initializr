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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StreamUtils;

public class MavenUtilsTests {

	@Test
	public void test() throws IOException, XmlPullParserException {

		final InputStream is = MavenUtilsTests.class.getResourceAsStream("/test-pom.xml");

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MavenUtils.addDockerPlugin(is, bos);

		final String result = new String(bos.toByteArray(), Charset.forName("utf-8"));

		final InputStream comparisonFile = MavenUtilsTests.class.getResourceAsStream("/test-pom-with-docker-plugin.xml");
		final String pomWithDockerPlugin = StreamUtils.copyToString(comparisonFile, Charset.forName("utf-8"));
		Assert.assertEquals(pomWithDockerPlugin, result);
	}

}
