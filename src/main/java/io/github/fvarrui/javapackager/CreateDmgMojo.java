package io.github.fvarrui.javapackager;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "create-dmg", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class CreateDmgMojo extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException {
		getLog().info("create-dmg mojooooo!");
	}

}
