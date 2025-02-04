package io.github.fvarrui.javapackager.gradle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.MacPackager;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Creates zipball (zip file)  on Gradle context
 */
public class CreateZipball extends ArtifactGenerator<Packager> {
	
	public CreateZipball() {
		super("Zipball");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getCreateZipball();
	}
	
	@Override
	protected File doApply(Packager packager) throws Exception {
		
		String name = packager.getName();
		String version = packager.getVersion();
		Platform platform = packager.getPlatform();
		File outputDirectory = packager.getOutputDirectory();
		File appFolder = packager.getAppFolder();
		File executable = packager.getExecutable();
		String jreDirectoryName = packager.getJreDirectoryName();

		String zipFileName = packager.getZipballName() != null ? packager.getZipballName() : name + "-" + version + "-" + platform + ".zip";
		File zipFile = new File(outputDirectory, zipFileName);

		try (OutputStream fos = Files.newOutputStream(zipFile.toPath());
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(bos)) {

			if (Platform.windows.equals(platform)) {
				Path basePath = appFolder.getParentFile().toPath();
				try (Stream<Path> fileStream = Files.walk(appFolder.toPath())) {
					fileStream.forEach(path -> {
						if (path.equals(zipFile.toPath())) {
							return;
						}
						File file = path.toFile();
						if (file.isFile()) {
							try {
								ZipArchiveEntry entry = new ZipArchiveEntry(path.toFile(), basePath.relativize(path).toString());
								zipOut.putArchiveEntry(entry);
								Files.copy(path, zipOut);
								zipOut.closeArchiveEntry();
							} catch (IOException e) {
								throw new UncheckedIOException(e);
							}
						}
					});
				}
			} else if (Platform.linux.equals(platform)) {
				Path appPath = appFolder.getParentFile().toPath();
				try (Stream<Path> fileStream = Files.walk(appPath)) {
					fileStream.forEach(path -> {
						if (path.equals(zipFile.toPath())) {
							return;
						}
						try {
							String relativePath = appPath.relativize(path).toString();
							if (path.toFile().isFile()) {
								if (!(relativePath.equals(executable.getName())
											  || relativePath.startsWith(jreDirectoryName + "/bin/")
											  || relativePath.startsWith("scripts/"))) {
									ZipArchiveEntry entry = new ZipArchiveEntry(path.toFile(), relativePath);
									if (relativePath.equals(executable.getName())
											|| relativePath.startsWith(jreDirectoryName + "/bin/")
											|| relativePath.startsWith("scripts/")) {
										entry.setUnixMode(0755);
									}
									zipOut.putArchiveEntry(entry);
									Files.copy(path, zipOut);
									zipOut.closeArchiveEntry();
								}
							}
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
				}
			} else if (Platform.mac.equals(platform)) {
				MacPackager macPackager = (MacPackager) packager;
				File appFile = macPackager.getAppFile();

				Path appPath = appFolder.toPath();
				try (Stream<Path> fileStream = Files.walk(appFolder.toPath())) {
					fileStream.forEach(path -> {
						if (path.equals(zipFile.toPath())) {
							return;
						}
						try {
							String relativePath = appPath.relativize(path).toString();
							if (path.toFile().isFile()) {
								if (!(relativePath.startsWith(appFile.getName() + "/Contents/MacOS/" + executable.getName())
											   || relativePath.startsWith(appFile.getName() + "/Contents/MacOS/universalJavaApplicationStub")
											   || relativePath.startsWith(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/")
											   || relativePath.startsWith(appFile.getName() + "/Contents/Resources/scripts/"))) {
									ZipArchiveEntry entry = new ZipArchiveEntry(path.toFile(), relativePath);
									if (relativePath.equals(executable.getName())
											|| relativePath.startsWith(jreDirectoryName + "/bin/")
											|| relativePath.startsWith("scripts/")) {
										entry.setUnixMode(0755);
									}
									zipOut.putArchiveEntry(entry);
									Files.copy(path, zipOut);
									zipOut.closeArchiveEntry();
								}
							}
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
				}
			}
		}

		return zipFile;
	}

}
