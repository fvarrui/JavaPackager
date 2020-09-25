package io.github.fvarrui.javapackager.packagers;

import java.io.File;

public abstract class ArtifactGenerator {

	private String artifactName;
	
	public ArtifactGenerator() {
		super();
	}
	
	public ArtifactGenerator(String artifactName) {
		super();
		this.artifactName = artifactName;
	}

	public String getArtifactName() {
		return artifactName;
	}

	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}

    public abstract File apply(Packager packager) throws Exception;
        
}
