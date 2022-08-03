package io.github.fvarrui.javapackager.utils.updater;

import io.github.fvarrui.javapackager.model.Platform;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class RealTest {
    @Test
    void test() throws Exception {

        // PUBLISH CURRENT JAVA PACKAGER TO LOCAL MAVEN REPO TO BE USED BY THE HELLO WORLD PROJECTS
        File gradlew = new File(System.getProperty("user.dir") +
                "/gradlew" + (Platform.getCurrentPlatform() == Platform.windows ? ".bat" : ".sh"));
        if (getBuilder(gradlew.getAbsolutePath(), "build", "publishToMavenLocal", "-x", "test", "-x", "javadoc")
                .start().waitFor()
                != 0) throw new Exception("Failed! Exit code is not 0, see details further below:");

        // PACKAGE GRADLE HELLO WORLD WITH CURRENT JAVA PACKAGER
        if (getBuilder(gradlew.getAbsolutePath(),
                "clean", "package", "-x", "test", "-x", "javadoc", "--stacktrace")
                .directory(new File(System.getProperty("user.dir") + "/test/hello-world-gradle"))
                .inheritIO().start().waitFor()
                != 0) throw new Exception("Failed! Exit code is not 0, see details further below:");

        // PACKAGE MAVEN HELLO WORLD WITH CURRENT JAVA PACKAGER
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(System.getProperty("user.dir") + "/test/hello-world-maven/pom.xml"));
        request.setGoals(Arrays.asList("clean", "package", "-Dmaven.javadoc.skip=true", "-Dmaven.test.skip=true"));
        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
    }

    private ProcessBuilder getBuilder(String... arguments) throws IOException {
        ProcessBuilder builder = new ProcessBuilder().command(arguments)
                .inheritIO();
        Map<String, String> environment = builder.environment();
        setValueIgnoreCase(environment, "JAVA_HOME", System.getProperty("java.home"));
        return builder;
    }

    private String getValueIgnoreCase(Map<String, String> map, String key) {
        for (String _key : map.keySet()) {
            if (_key != null && _key.equalsIgnoreCase(key))
                return map.get(_key);
        }
        return null;
    }

    private void setValueIgnoreCase(Map<String, String> map, String key, String value) {
        for (String _key : map.keySet()) {
            if (_key != null && _key.equalsIgnoreCase(key)) {
                map.put(_key, value);
                return;
            }
        }
    }

}
