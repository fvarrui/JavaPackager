package io.github.fvarrui.javapackager.generator;

import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
@Inherited
public @interface Plugin {

    // MAVEN:
    String name();

    LifecyclePhase defaultPhase() default LifecyclePhase.NONE;

    ResolutionScope requiresDependencyResolution() default ResolutionScope.NONE;

    ResolutionScope requiresDependencyCollection() default ResolutionScope.NONE;

    InstantiationStrategy instantiationStrategy() default InstantiationStrategy.PER_LOOKUP;

    String executionStrategy() default "once-per-session";

    boolean requiresProject() default true;

    boolean requiresReports() default false;

    boolean aggregator() default false;

    boolean requiresDirectInvocation() default false;

    boolean requiresOnline() default false;

    boolean inheritByDefault() default true;

    String configurator() default "";

    boolean threadSafe() default false;

    // GRADLE:
    String groupName() default "JavaPackager";
    String settingsExtName() default "javapackager";
    String taskName() default "package";
    String description() default "Packages the application as a native Windows, Mac OS X or GNU/Linux executable and creates an installer.";
}
