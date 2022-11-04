package io.github.fvarrui.javapackager;

import io.github.fvarrui.javapackager.maven.MavenContext;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.Map;

import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;

@org.apache.maven.plugins.annotations.Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class MavenPackageTask extends PackageTask implements Mojo, ContextEnabled, PackagerFactory {
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;
    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;
    @Component
    private BuildPluginManager pluginManager;

    private Log log;
    private Map pluginContext;

    public MavenPackageTask() throws IOException {
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Context.setContext(
                new MavenContext(
                        executionEnvironment(mavenProject, mavenSession, pluginManager),
                        getLog()
                )
        );
        try {
            Packager packager = this.createPackager(this);
            // generate app, installers and bundles
            packager.createApp();
            packager.generateInstallers();
            packager.createBundles();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public Log getLog() {
        if (this.log == null) {
            this.log = new SystemStreamLog();
        }

        return this.log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Map getPluginContext() {
        return this.pluginContext;
    }

    public void setPluginContext(Map pluginContext) {
        this.pluginContext = pluginContext;
    }


}
