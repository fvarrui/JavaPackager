package io.github.fvarrui.javapackager;

import com.squareup.javapoet.*;
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.generator.*;
import io.github.fvarrui.javapackager.gradle.GradleContext;
import io.github.fvarrui.javapackager.packagers.Context;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;

import javax.lang.model.element.Modifier;

/**
 * This class is temporary, and I am not sure
 * if this makes into the final pull request.
 */
public class Main {

    public static void newMain(String[] args) throws IOException, ClassNotFoundException {
        File dir = new File(System.getProperty("user.dir")+"/src/main/java/io/github/fvarrui/javapackager");
        List<PluginClassFile> pluginClasses = getPluginClasses(dir);
        for (PluginClassFile pluginClassFile : pluginClasses) {
            generateMavenPluginClass(pluginClassFile);
            generateGradlePluginClass(pluginClassFile);
        }
    }

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
            if(method.getModifiers() == java.lang.reflect.Modifier.PRIVATE) continue;
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

    /**
     * Expects a class with the {@link Plugin} annotation
     * to generate the gradle plugin class from in the desired output directory.
     */
    private static void generateGradlePluginClass(PluginClassFile pluginClassFile) throws IOException, ClassNotFoundException {
        Class<?> pluginClass = pluginClassFile.loadClass();
        Field[] fields = pluginClass.getFields();
        Plugin pluginAnnotation = pluginClass.getAnnotation(Plugin.class);

        String simpleClassName = "Gradle"+pluginClass.getSimpleName();
        TypeSpec.Builder genClass = TypeSpec.classBuilder(simpleClassName)
                .addJavadoc("This is a generated class thus modifying it, is not recommended." +
                        " Instead the actual class {@link "+pluginClass.getName()+"} should be edited.")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(DefaultTask.class)
                .addSuperinterface(ParameterizedTypeName.get(org.gradle.api.Plugin.class, org.gradle.api.Project.class));

        genClass.addMethod(MethodSpec.constructorBuilder()
                        .addStatement("setGroup($S)", pluginAnnotation.groupName())
                        .addStatement("setDescription(§S)", pluginAnnotation.description())
                        .addStatement("getOutputs().upToDateWhen(o -> false)")
                .build());

        genClass.addMethod(MethodSpec.methodBuilder("apply")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(org.gradle.api.Project.class, "project")
                .addStatement("$T.setContext(new $T(project))", Context.class, GradleContext.class)
                .addCode("project.getPluginManager().apply(\"java\");\n" +
                        "\t\tproject.getPluginManager().apply(\"edu.sc.seis.launch4j\");")
                .addStatement(simpleClassName+" task = ("+simpleClassName+") project.getTasks().create($S, "+simpleClassName+".class).dependsOn(\"build\")", pluginAnnotation.taskName())
                .addCode("project.getExtensions().add("+pluginAnnotation.settingsExtName()+", task); // Use task instance as extension")
                .addStatement("$T.getGradleContext().setLibraryTask(project.getTasks().create(\"launch4j_\" + UUID.randomUUID(), $T.class));", Context.class, Launch4jLibraryTask.class)
                .build());

        for (Field field : getFieldsWithAnnotation(fields, Input.class)) {
            List<Class<?>> annotations = new ArrayList<>();
            if(field.getType().equals(File.class))
                if(field.isAnnotationPresent(Directory.class))
                    annotations.add(org.gradle.api.tasks.InputDirectory.class);
                else
                    annotations.add(org.gradle.api.tasks.InputFile.class);
            else
                annotations.add(org.gradle.api.tasks.Input.class);

            if(!field.isAnnotationPresent(Required.class))
                annotations.add(org.gradle.api.tasks.Optional.class);

            genClass.addField(fieldWithAnnotations(field.getType(), field.getName(), annotations).build());
            genClass.addMethod(getter(field.getType(), field.getName()).build());
            genClass.addMethod(modernSetter(field.getName()).build());
            if(field.isAnnotationPresent(Closure.class))
                genClass.addMethod(gradleClosureSetter(field.getType(), field.getName()).build());
        }

        JavaFile javaFile = JavaFile.builder(pluginClassFile.packageName, genClass.build())
                .build();
        javaFile.writeTo(new File(pluginClassFile.getParentFile()+"/"+simpleClassName+".java"));
    }

    private static MethodSpec.Builder gradleClosureSetter(Class<?> type, String fieldName) {
        return MethodSpec.methodBuilder(fieldName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(type)
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(groovy.lang.Closure.class, type), "closure").build())
                .addCode("this."+fieldName+" = new "+type.getName()+"();\n" +
                        GradleContext.class.getName()+".getGradleContext().getProject().configure("+fieldName+", closure);\n" +
                        "return "+fieldName+";");
    }

    private static FieldSpec.Builder fieldWithAnnotations(Class<?> type, String name, List<Class<?>> annotations) {
        FieldSpec.Builder builder = FieldSpec.builder(type, name);
        if(annotations!=null)
            for (Class<?> annotation : annotations) {
                builder.addAnnotation(annotation);
            }
        return builder;
    }

    // setter for field
    private static MethodSpec.Builder modernSetter(String fieldName) {
        return MethodSpec.methodBuilder(fieldName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addCode("this."+fieldName+" = "+fieldName+";");
    }

    // getter for field
    private static MethodSpec.Builder getter(Class<?> returnType, String fieldName) {
        String methodName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
        return MethodSpec.methodBuilder("get"+methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addCode("return "+fieldName+";");
    }

    private static List<Field> getFieldsWithAnnotation(Field[] fields, Class<? extends Annotation> annotationClass) {
        List<Field> list = new ArrayList<>();
        for (Field field : fields) {
            if(field.isAnnotationPresent(annotationClass)){
                list.add(field);
            }
        }
        return list;
    }

    /**
     * Expects a class with the {@link io.github.fvarrui.javapackager.generator.Plugin} annotation
     * to generate the maven plugin class from in the desired output directory.
     */
    private static void generateMavenPluginClass(PluginClassFile pluginClass) {

    }

    /**
     * Recursively searches the provided directory for
     * files containing the {@link io.github.fvarrui.javapackager.generator.Plugin} annotation.
     */
    private static List<PluginClassFile> getPluginClasses(File dir) throws IOException {
        if(dir.isFile()) throw new IllegalArgumentException("Parameter 'dir' must be a directory and not a file!");
        List<PluginClassFile> files = new ArrayList<>();
        for (File f : dir.listFiles()) {
            if(f.isDirectory()) files.addAll(getPluginClasses(f));
            else{
                List<String> lines = Files.readAllLines(f.toPath());
                PluginClassFile pluginClassFile = null;
                for (String line : lines) {
                    if(line.contains("io.github.fvarrui.javapackager.generator.Plugin")) {
                        pluginClassFile = new PluginClassFile(f.getAbsolutePath(), null, dir);
                        break;
                    }
                }
                if(pluginClassFile != null) {
                    String packageName = null;
                    for (String line : lines) {
                        if(line.contains("package")){
                            packageName = line.replace("package ", "").replace(";", "").trim();
                            break;
                        }
                    }
                    Objects.requireNonNull(packageName);
                    pluginClassFile.packageName = packageName;
                    files.add(pluginClassFile);
                }
            }
        }
        return files;
    }
}
