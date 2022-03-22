package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.zip.UnixStat;
import org.vafer.jdeb.Console;
import org.vafer.jdeb.DataProducer;
import org.vafer.jdeb.DebMaker;
import org.vafer.jdeb.ant.Data;
import org.vafer.jdeb.ant.Mapper;
import org.vafer.jdeb.mapping.PermMapper;
import org.vafer.jdeb.producers.DataProducerLink;

import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates a DEB package file including all app folder's content only for 
 * GNU/Linux so app could be easily distributed on Gradle context
 */
public class GenerateDeb extends ArtifactGenerator<LinuxPackager> {

	private Console console;
	
	public GenerateDeb() {
		super("DEB package");
		console = new Console() {
			
			@Override
			public void warn(String message) {
				Logger.warn(message);
			}
			
			@Override
			public void info(String message) {
				Logger.info(message);
			}
			
			@Override
			public void debug(String message) {
				Logger.debug(message);
			}
			
		};
	}
	
	@Override
	public boolean skip(LinuxPackager packager) {
		return !packager.getLinuxConfig().isGenerateDeb();
	}
	
	@Override
	protected File doApply(LinuxPackager packager) throws Exception {
		
		File assetsFolder = packager.getAssetsFolder();
		String name = packager.getName();
		File appFolder = packager.getAppFolder();
		File outputDirectory = packager.getOutputDirectory();
		String version = packager.getVersion();
		boolean bundleJre = packager.getBundleJre();
		String jreDirectoryName = packager.getJreDirectoryName();
		File executable = packager.getExecutable();
		File javaFile = new File(appFolder, jreDirectoryName + "/bin/java");

		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, packager);
		Logger.info("Desktop file rendered in " + desktopFile.getAbsolutePath());
		
		// generates deb control file from velocity template
		File controlFile = new File(assetsFolder, "control");
		VelocityUtils.render("linux/control.vtl", controlFile, packager);
		Logger.info("Control file rendered in " + controlFile.getAbsolutePath());

		// generated deb file
		File debFile = new File(outputDirectory, name + "_" + version + ".deb");

		// create data producers collections
		
		List<DataProducer> conffilesProducers = new ArrayList<>();		
		List<DataProducer> dataProducers = new ArrayList<>();		
		
		// builds app folder data producer, except executable file and jre/bin/java
		
		Mapper appFolderMapper = new Mapper();
		appFolderMapper.setType("perm");
		appFolderMapper.setPrefix("/opt/" + name);
		appFolderMapper.setFileMode("644");
		
		Data appFolderData = new Data();
		appFolderData.setType("directory");
		appFolderData.setSrc(appFolder);
		appFolderData.setExcludes(executable.getName() + (bundleJre ? "," + jreDirectoryName + "/bin/java" + "," + jreDirectoryName + "/lib/jspawnhelper" : ""));
		appFolderData.addMapper(appFolderMapper);

		dataProducers.add(appFolderData);

		// builds executable data producer
		
		Mapper executableMapper = new Mapper();
		executableMapper.setType("perm");
		executableMapper.setPrefix("/opt/" + name);
		executableMapper.setFileMode("755");
		
		Data executableData = new Data();
		executableData.setType("file");
		executableData.setSrc(new File(appFolder.getAbsolutePath() + "/" + name));
		executableData.addMapper(executableMapper);

		dataProducers.add(executableData);

		// desktop file data producer 

		Mapper desktopFileMapper = new Mapper();
		desktopFileMapper.setType("perm");
		desktopFileMapper.setPrefix("/usr/share/applications");
		
		Data desktopFileData = new Data();
		desktopFileData.setType("file");
		desktopFileData.setSrc(desktopFile);
		desktopFileData.addMapper(desktopFileMapper);
		
		dataProducers.add(desktopFileData);

		// java binary file data producer
		
		if (bundleJre) {
			
			Mapper javaBinaryMapper = new Mapper();
			javaBinaryMapper.setType("perm");
			javaBinaryMapper.setFileMode("755");
			javaBinaryMapper.setPrefix("/opt/" + name + "/" + jreDirectoryName + "/bin");
			
			Data javaBinaryData = new Data();
			javaBinaryData.setType("file");
			javaBinaryData.setSrc(javaFile);
			javaBinaryData.addMapper(javaBinaryMapper);

			dataProducers.add(javaBinaryData);
			
			// set correct permissions on jre/lib/jspawnhelper
			Mapper javaSpawnHelperMapper = new Mapper();
			javaSpawnHelperMapper.setType("perm");
			javaSpawnHelperMapper.setFileMode("755");
			javaSpawnHelperMapper.setPrefix("/opt/" + name + "/" + jreDirectoryName + "/lib");

			File jSpawnHelperFile = new File(appFolder, jreDirectoryName + "/lib/jspawnhelper");

			Data javaSpawnHelperData = new Data();
			javaSpawnHelperData.setType("file");
			javaSpawnHelperData.setSrc(jSpawnHelperFile);
			javaSpawnHelperData.addMapper(javaSpawnHelperMapper);

			dataProducers.add(javaSpawnHelperData);
			
		}
		
		// symbolic link in /usr/local/bin to app binary data producer

        DataProducer linkData = createLink("/usr/local/bin/" + name, "/opt/" + name + "/" + name);
        
		dataProducers.add(linkData);
		
		// builds deb file
		
        DebMaker debMaker = new DebMaker(console, dataProducers, conffilesProducers);
        debMaker.setDeb(debFile);
        debMaker.setControl(controlFile.getParentFile());
        debMaker.setCompression("gzip");
        debMaker.setDigest("SHA256");
        debMaker.validate();
        debMaker.makeDeb();

		return debFile;

	}
	
	private DataProducer createLink(String name, String target) {
		int linkMode = UnixStat.LINK_FLAG | Integer.parseInt("777", 8);		
		org.vafer.jdeb.mapping.Mapper linkMapper = new PermMapper(
				0, 0, 					// uid, gid 
				"root", "root", 		// user, group
				linkMode, linkMode, 	// perms 
				0, null
			);
        return new DataProducerLink(
        		name,		// link name 
        		target, 	// target
        		true, 		// symbolic link
        		null, null, 
        		new org.vafer.jdeb.mapping.Mapper[] { linkMapper }	// link mapper
    		);
	}
	
}
