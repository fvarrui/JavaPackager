package io.github.fvarrui.javapackager;

import com.squareup.javapoet.*;
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.generator.*;
import io.github.fvarrui.javapackager.gradle.GradleContext;
import io.github.fvarrui.javapackager.packagers.Context;
import org.apache.maven.plugin.Mojo;
import org.gradle.api.DefaultTask;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void aa() throws IOException {
        String simpleClassName = "GradlePackageTask";
        TypeSpec.Builder genClass = TypeSpec.classBuilder(simpleClassName)
                .addJavadoc("This is a generated class thus modifying it, is not recommended." +
                        " Instead the actual class {@link ...} should be edited.")
                .addModifiers(Modifier.PUBLIC)
                .superclass(DefaultTask.class)
                .addSuperinterface(ParameterizedTypeName.get(org.gradle.api.Plugin.class, org.gradle.api.Project.class))
                .addAnnotation(AnnotationSpec.builder(Mojo.class)
                        .addMember("name", "pluginAnnotation.name()")
                        .addMember("defaultPhase", "pluginAnnotation.defaultPhase().id()")
                        .addMember("requiresDependencyResolution", "pluginAnnotation.requiresDependencyResolution().id()")
                        .build());

        //TypeSpec.Builder genClassInterface = TypeSpec.interfaceBuilder(pluginClass.getSimpleName() + "Methods");

        genClass.addMethod(MethodSpec.constructorBuilder()
                .addStatement("setGroup($S)", "pluginAnnotation.groupName()")
                .addStatement("setDescription(§S)", "pluginAnnotation.description()")
                .addStatement("getOutputs().upToDateWhen(o -> false)")
                .build());

        genClass.addMethod(MethodSpec.methodBuilder("apply")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(org.gradle.api.Project.class, "project")
                .addStatement("$T.setContext(new $T(project))", Context.class, GradleContext.class)
                .addCode("project.getPluginManager().apply(\"java\");\n" +
                        "\t\tproject.getPluginManager().apply(\"edu.sc.seis.launch4j\");\n")
                .addStatement(simpleClassName+" task = ("+simpleClassName+") project.getTasks().create($S, "+simpleClassName+".class).dependsOn(\"build\")", "pluginAnnotation.taskName()")
                .addCode("project.getExtensions().add(pluginAnnotation.settingsExtName(), task); // Use task instance as extension\n")
                .addStatement("$T.getGradleContext().setLibraryTask(project.getTasks().create(\"launch4j_\" + UUID.randomUUID(), $T.class))", Context.class, Launch4jLibraryTask.class)
                .build());

        JavaFile javaFile = JavaFile.builder("com.my.package", genClass.build())
                .build();
        javaFile.writeTo(System.out);
    }
}