package io.github.fvarrui.javapackager.gradle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.MacPackager;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Creates tarball (tar.gz file) on Gradle context 
 */
public class CreateTarball extends ArtifactGenerator<Packager> {
	
	public CreateTarball() {
		super("Tarball");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getCreateTarball();
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

		// tgz file name
		String finalName = packager.getTarballName() != null ? packager.getTarballName() : name + "-" + version + "-" + platform;
		String format = ".tar.gz";
		File tarFile = new File(outputDirectory, finalName + format);

		try (OutputStream fos = Files.newOutputStream(tarFile.toPath());
			 BufferedOutputStream bos = new BufferedOutputStream(fos);
			 GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(bos);
			 TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gcos)) {

			if (Platform.windows.equals(platform)) {
				Path basePath = appFolder.getParentFile().toPath();
				try (Stream<Path> fileStream = Files.walk(appFolder.toPath())) {
					fileStream.forEach(path -> {
                        if (path.equals(tarFile.toPath())) {
                            return;
                        }
						File file = path.toFile();
						if (file.isFile()) {
							try {
								TarArchiveEntry entry = new TarArchiveEntry(path.toFile(), basePath.relativize(path).toString());
								tarOut.putArchiveEntry(entry);
								Files.copy(path, tarOut);
								tarOut.closeArchiveEntry();
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
						if (path.equals(tarFile.toPath())) {
							return;
						}
						try {
							String relativePath = appPath.relativize(path).toString();
							if (path.toFile().isFile()) {
								if (!(relativePath.equals(executable.getName())
											  || relativePath.startsWith(jreDirectoryName + "/bin/")
											  || relativePath.startsWith("scripts/"))) {
									TarArchiveEntry entry = new TarArchiveEntry(path.toFile(), relativePath);
									if (relativePath.equals(executable.getName())
											|| relativePath.startsWith(jreDirectoryName + "/bin/")
											|| relativePath.startsWith("scripts/")) {
										entry.setMode(0755);
									}
									tarOut.putArchiveEntry(entry);
									Files.copy(path, tarOut);
									tarOut.closeArchiveEntry();
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
						if (path.equals(tarFile.toPath())) {
							return;
						}
						try {
							String relativePath = appPath.relativize(path).toString();
							if (path.toFile().isFile()) {
								if (!(relativePath.startsWith(appFile.getName() + "/Contents/MacOS/" + executable.getName())
											  || relativePath.startsWith(appFile.getName() + "/Contents/MacOS/universalJavaApplicationStub")
											  || relativePath.startsWith(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/")
											  || relativePath.startsWith(appFile.getName() + "/Contents/Resources/scripts/"))) {
									TarArchiveEntry entry = new TarArchiveEntry(path.toFile(), relativePath);
									if (relativePath.equals(executable.getName())
											|| relativePath.startsWith(jreDirectoryName + "/bin/")
											|| relativePath.startsWith("scripts/")) {
										entry.setMode(0755);
									}
									tarOut.putArchiveEntry(entry);
									Files.copy(path, tarOut);
									tarOut.closeArchiveEntry();
								}
							}
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
				}
			}
		}

		return tarFile;
	}

}
