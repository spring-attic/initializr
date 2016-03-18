package io.spring.initializr.generator;

import java.io.File;

public class ScsProjectGenerator extends ProjectGenerator {

	@Override
	protected File doGenerateProjectStructure(ProjectRequest request) {
		//Docker assembly file
		final File rootDir = super.doGenerateProjectStructure(request);
		
		final File dir = new File(rootDir, request.getBaseDir());
				
		final File dockerDir = new File(dir, "src/main/docker");
		dockerDir.mkdirs();
		write(new File(dockerDir, "assembly.xml"), "assembly.xml", initializeModel(request));

		return rootDir;
	}


}
