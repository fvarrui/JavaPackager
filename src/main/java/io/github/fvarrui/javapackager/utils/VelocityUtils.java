package io.github.fvarrui.javapackager.utils;

import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.util.StringBuilderWriter;

/**
 * Velocity utils 
 */
public class VelocityUtils {

	private static File assetsDir = new File("assets");
	private static VelocityEngine velocityEngine = null;

	private static VelocityEngine getVelocityEngine() {
		
		if (velocityEngine == null) {
			
			velocityEngine = new VelocityEngine();
			
			// specify resource loaders to use
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "file,class");
			
			// for the loader 'file', set the FileResourceLoader as the class to use and use 'assets' directory for templates
			velocityEngine.setProperty("resource.loader.file.class", FileResourceLoader.class.getName());
			velocityEngine.setProperty("resource.loader.file.path", assetsDir.getAbsolutePath());
			
			// for the loader 'class', set the ClasspathResourceLoader as the class to use
			velocityEngine.setProperty("resource.loader.class.class", ClasspathResourceLoader.class.getName());
			
			velocityEngine.init();
			
		}
		
		return velocityEngine;
	}
	
	private static String render(String templatePath, Object info) throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("features", new ArrayList<String>());
		context.put("GUID", UUID.class);
		context.put("StringUtils", org.apache.commons.lang3.StringUtils.class);
		context.put("info", info);
		Template template = getVelocityEngine().getTemplate(templatePath, "UTF-8");
		StringBuilderWriter writer = new StringBuilderWriter();
		template.merge(context, writer);		
		return writer.toString();
	}

	public static void setAssetsDir(File assetsDir) {
		VelocityUtils.assetsDir = assetsDir;
	}

	public static void render(String templatePath, File output, Object info) throws Exception {
		render(templatePath, output, info, false);
	}
	
	public static void render(String templatePath, File output, Object info, boolean includeBom) throws Exception {
		String data = render(templatePath, info);
		data = StringUtils.dosToUnix(data);
		if (!includeBom) {
			writeStringToFile(output, data, "UTF-8");
		} else {
			FileUtils.writeStringToFileWithBOM(output, data);
		}
	}
	
}
