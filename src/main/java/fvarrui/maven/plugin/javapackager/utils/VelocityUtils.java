package fvarrui.maven.plugin.javapackager.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityUtils {

	private static VelocityEngine velocityEngine;

	static {
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
	}

	public static void render(String templatePath, File output, String prefix, Object data) throws MojoExecutionException {
		try {

			VelocityContext context = new VelocityContext();
			context.put(prefix, data);

			Template template = velocityEngine.getTemplate(templatePath, "UTF-8");

			FileWriter fw = new FileWriter(output);

			template.merge(context, fw);

			fw.flush();
			fw.close();

		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
