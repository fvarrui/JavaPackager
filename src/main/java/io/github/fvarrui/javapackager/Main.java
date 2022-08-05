package io.github.fvarrui.javapackager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is temporary, and I am not sure
 * if this makes into the final pull request.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File generatedDir = new File(System.getProperty("user.dir")+"/src/main/java/io/github/fvarrui/javapackager/generated");
        FileUtils.deleteDirectory(generatedDir);
        generatedDir.mkdirs();
        File filePackageTask = new File(generatedDir.getParentFile()+"/PackageTask.java");
        List<String> lines = new CopyOnWriteArrayList<>(Files.readAllLines(filePackageTask.toPath()));
        int linesToRemove = 0;
        for (String line : lines) {
            if(linesToRemove > 0){
                lines.remove(line);
                linesToRemove--;
            }
            else if(line.contains("org.gradle.api")) lines.remove(line);
            else if(line.contains("if(isGradle){")){
                lines.remove(line);
                linesToRemove = 4; // Remove next 4 lines
            }
            else if(line.contains(" Project ") || line.contains("@OutputFiles")
            || line.contains("@TaskAction") || line.contains("@OutputDirectory") || line.contains("@InputDirectory") || line.contains("@Optional")
            || line.contains("@InputFile") || line.contains("@Input") || line.contains("gradleProject"))
                lines.remove(line);
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if(line.contains("extends DefaultTask")) {
                line = line.replace("extends DefaultTask", "");
                lines.set(i, line);
            }
            if(line.contains("isGradle ?")){
                int iStart = line.indexOf("isGradle ?");
                int iEnd = line.indexOf(":");
                line = line.substring(0, iStart) + line.substring(iEnd + 1);
                lines.set(i, line);
            }
            if(line.contains("package io.github.fvarrui.javapackager;")){
                line = line.replace("package io.github.fvarrui.javapackager;", "package io.github.fvarrui.javapackager.generated;");
                lines.set(i, line);
            }
            if(line.contains("PackageTask")){
                line = line.replace("PackageTask", "MavenPackageTask");
                lines.set(i, line);
            }
        }

        File generatedFilePackageTask = new File(generatedDir+"/MavenPackageTask.java");
        generatedFilePackageTask.createNewFile();
        Files.write(generatedFilePackageTask.toPath(), lines);

        File generatedFilePackageTaskFunctions = new File(generatedDir+"/PackageTaskFunctions.java");
        StringBuilder builder = new StringBuilder();
        builder.append("package io.github.fvarrui.javapackager.generated;\n");
        builder.append("public interface PackageTaskFunctions{\n");
        for (Method method : PackageTask.class.getMethods()) {
            if(method.getModifiers() == Modifier.PRIVATE) continue;
            if(!method.getDeclaringClass().equals(PackageTask.class)) continue;
            String methodString = "";
            methodString +=method.getReturnType().getName()+" ";
            methodString +=method.getName()+"(";


            String params = "";
            for (Parameter parameter : method.getParameters()) {
                params += (parameter.getParameterizedType().getTypeName()
                        +" "+parameter.getName()+",");
            }
            if(!params.isEmpty()){
                params = params.substring(0, params.length()-1);
                methodString +=params;
            }
            methodString += ")";
            methodString = methodString.replace("io.github.fvarrui.javapackager.PackageTask",
                    "io.github.fvarrui.javapackager.generated.PackageTaskFunctions");


            String exceptions = "";
            for (Class<?> exceptionType : method.getExceptionTypes()) {
                exceptions += (exceptionType.getName()+",");
            }
            if(!exceptions.isEmpty()){
                exceptions = exceptions.substring(0, exceptions.length()-1);
                methodString += " throws "+ exceptions;
            }


            builder.append(methodString+";\n");
        }
        builder.append("}\n");
        generatedFilePackageTaskFunctions.createNewFile();
        Files.write(generatedFilePackageTaskFunctions.toPath(), builder.toString().getBytes(StandardCharsets.UTF_8));
    }
}
