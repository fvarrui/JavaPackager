package fvarrui.maven.plugin.javapackager.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

public class VelocityUtils {

	private static VelocityEngine velocityEngine;

	static {
		velocityEngine = new VelocityEngine();
		
		// specify resource loaders to use
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,class");
		
		// for the loader 'file', set the FileResourceLoader as the class to use and use 'assets' directory for templates
		velocityEngine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
		velocityEngine.setProperty("file.resource.loader.path", "assets");
		
		// for the loader 'class', set the ClasspathResourceLoader as the class to use
		velocityEngine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
		
		velocityEngine.init();
	}

	public static void render(String templatePath, File output, Map<String, Object> info) throws MojoExecutionException {
		try {
			
			VelocityContext context = new VelocityContext();
			context.put("info", info);

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
