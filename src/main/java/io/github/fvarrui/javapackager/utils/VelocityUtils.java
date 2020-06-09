package io.github.fvarrui.javapackager.utils;

import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.util.StringBuilderWriter;

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

	public static void render(String templatePath, File output, Object info) throws MojoExecutionException {
		try {
			String data = render(templatePath, info);
			data = data.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
			writeStringToFile(output, data, "UTF-8");
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private static String render(String templatePath, Object info) throws MojoExecutionException {
		VelocityContext context = new VelocityContext();
		context.put("features", new ArrayList<String>());
		context.put("GUID", UUID.class);
		context.put("StringUtils", StringUtils.class);
		context.put("info", info);
		Template template = velocityEngine.getTemplate(templatePath, "UTF-8");
		StringBuilderWriter writer = new StringBuilderWriter();
		template.merge(context, writer);		
		return writer.toString();
	}
	
}
