package io.github.fvarrui.javapackager;

import io.github.fvarrui.javapackager.model.Platform;
import org.apache.maven.shared.invoker.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

public class RealTest {

    @Test
    void helloWorldMaven() throws Exception {
        publishPluginLocally();
        // PACKAGE MAVEN HELLO WORLD WITH CURRENT JAVA PACKAGER
        InvocationRequest request = new DefaultInvocationRequest();
        request.setMavenHome(findMavenHome());
        request.setJavaHome(new File(System.getProperty("java.home")));
        request.setPomFile(new File(System.getProperty("user.dir") + "/test/hello-world-maven/pom.xml"));
        request.setGoals(Arrays.asList("clean", "package"));
        request.addArg("-Dmaven.javadoc.skip=true");
        request.addArg("-Dmaven.test.skip=true");
        request.addArg("-e");
        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0 || result.getExecutionException() != null)
            throw new RuntimeException("Maven exit code != 0, see the cause below for details.", result.getExecutionException());
    }

    @Test
    void helloWorldGradle() throws Exception {
        publishPluginLocally();
        // PACKAGE GRADLE HELLO WORLD WITH CURRENT JAVA PACKAGER
        MyProcess p = new MyProcess(getGradlew().getAbsolutePath(),
                "clean", "package", "-x", "test", "-x", "javadoc", "--stacktrace");
        p.builder.directory(new File(System.getProperty("user.dir") + "/test/hello-world-gradle"));
        p.execNow();
    }

    @Test
    void publishPluginLocally() throws Exception {
        System.out.println("Building and publishing plugin locally...");
        // PUBLISH CURRENT JAVA PACKAGER TO LOCAL MAVEN REPO TO BE USED BY THE HELLO WORLD PROJECTS
        MyProcess p = new MyProcess(getGradlew().getAbsolutePath(), "build", "publishToMavenLocal", "-x", "validatePlugins", "-x", "test", "-x", "javadoc", "--stacktrace");
        p.execNow();
        System.out.println("Successfully built and published plugin locally.");
    }

    private File getGradlew() {
        return new File(System.getProperty("user.dir") +
                "/gradlew" + (Platform.getCurrentPlatform() == Platform.windows ? ".bat" : ".sh"));
    }

    private File findMavenHome() {
        File startDir;
        if (Platform.getCurrentPlatform() == Platform.windows) {
            startDir = new File(System.getProperty("user.home") + "\\.m2\\wrapper\\dists");
            return startDir.listFiles()[0].listFiles()[0].listFiles()[0];
        } else { // LINUX OR MAC
            // TODO
            throw new RuntimeException("Failed to determine maven home folder! Linux is currently not supported.");
        }
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

    class MyProcess {
        public ProcessBuilder builder;

        public MyProcess(String... arguments) {
            this(new ProcessBuilder().command(arguments));
        }

        public MyProcess(ProcessBuilder builder) {
            this.builder = builder;
            builder.redirectError(ProcessBuilder.Redirect.PIPE);
            builder.redirectInput(ProcessBuilder.Redirect.PIPE);
            Map<String, String> environment = builder.environment();
            setValueIgnoreCase(environment, "JAVA_HOME", System.getProperty("java.home"));
        }

        public String commandToString() {
            StringBuilder sb = new StringBuilder();
            for (String s : builder.command()) {
                sb.append(s).append(" ");
            }
            return sb.toString();
        }

        public void execNow() throws IOException, InterruptedException {
            try {
                StackTraceElement[] stackTrace = new Exception().getStackTrace();
                StackTraceElement stackEl = stackTrace[1];
                System.out.println(stackEl.toString()+" -> "+commandToString());
                Process p = builder.start();
                // Inheriting IO doesn't work as good as doing the below:
                new Thread(() -> {
                    try{
                        try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))){
                            String line = null;
                            while ((line = reader.readLine()) != null){
                                System.out.println(line);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                new Thread(() -> {
                    try{
                        try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))){
                            String line = null;
                            while ((line = reader.readLine()) != null){
                                System.err.println(line);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                int result = p.waitFor();
                if (result != 0)
                    throw new IOException("Process exited with " + result + " instead of 0! Something probably went wrong.");
            } catch (Throwable e) {
                System.err.println("Error during execution of: " + commandToString());
                throw e;
            }
        }
    }

}
