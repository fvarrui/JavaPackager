package io.github.fvarrui.javapackager.gradle;

import io.github.fvarrui.javapackager.packagers.LocalContext;
import io.github.fvarrui.javapackager.utils.Logger;
import org.gradle.api.Task;
import org.gradle.api.file.*;

import java.io.File;

public class GradleLocalContext implements LocalContext {
    final FileSystemOperations fileSystemOperations;

    public GradleLocalContext(FileSystemOperations fileSystemOperations) {
        this.fileSystemOperations = fileSystemOperations;
    }

    public FileSystemOperations getFileSystemOperations() {
        return fileSystemOperations;
    }

    public void copyAdditionalResources(Object o, File destination) throws Exception {
        if (o instanceof CopySpec) {
            getFileSystemOperations().copy(cs -> {
                cs.with((CopySpec) o);
                cs.into(destination);
                cs.eachFile(fcd -> {
                    if (fcd.isDirectory()) {
                        Logger.info("Copying folder [" + fcd.getPath() + "] to folder [" + destination + "]");
                    } else {
                        Logger.info("Copying file [" + fcd.getPath() + "] to folder [" + destination + "]");
                    }
                });
            });
        } else if (o instanceof FileCollection) {
            getFileSystemOperations().copy(cs -> {
                cs.from((FileCollection) o);
                cs.into(destination);
                cs.eachFile(fcd -> {
                    if (fcd.isDirectory()) {
                        Logger.info("Copying folder [" + fcd.getPath() + "] to folder [" + destination + "]");
                    } else {
                        Logger.info("Copying file [" + fcd.getPath() + "] to folder [" + destination + "]");
                    }
                });
            });
        } else if (o instanceof Task) {
            getFileSystemOperations().copy(cs -> {
                cs.from((Task) o);
                cs.into(destination);
                cs.eachFile(fcd -> {
                    if (fcd.isDirectory()) {
                        Logger.info("Copying folder [" + fcd.getPath() + "] to folder [" + destination + "]");
                    } else {
                        Logger.info("Copying file [" + fcd.getPath() + "] to folder [" + destination + "]");
                    }
                });
            });
        } else {
            throw new IllegalArgumentException("Unknown resource type: " + o);
        }
    }
    
    public File getSingleFile(Object o) {
        if (o instanceof FileCollection) {
            return  ((FileCollection) o).getSingleFile();
        } else if (o instanceof Task) {
            return  ((Task) o).getOutputs().getFiles().getSingleFile();
        } /*else if (o instanceof Configuration) {
            Configuration c = ((Configuration) o);
            c.resolve()
        }*/

        return null;
    }
}
