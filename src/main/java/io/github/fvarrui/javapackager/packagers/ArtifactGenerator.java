package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.utils.Logger;


/**
 * Artifact generation base class 
 */
public abstract class ArtifactGenerator<T extends Packager> {

	private String artifactName;
	
	public ArtifactGenerator() {
		super();
	}
	
	public ArtifactGenerator(String artifactName) {
		super();
		this.artifactName = artifactName;
	}
	
	public boolean skip(T packager) {
		return false;
	}

	public String getArtifactName() {
		return artifactName;
	}

	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}

	protected abstract File doApply(T packager) throws Exception;
    
    @SuppressWarnings("unchecked")
	public File apply(Packager packager) throws Exception {
    	if (skip((T)packager)) {
			Logger.warn(getArtifactName() + " artifact generation skipped!");    		
    		return null;
    	}
    	return doApply((T)packager);
    }
        
}
