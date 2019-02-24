package org.codehaus.mojo.exec;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.artifact.MavenMetadataSource;

/**
 * Executes the supplied java class in the current VM with the enclosing project's dependencies as classpath.
 * 
 * @author Kaare Nilsen (kaare.nilsen@gmail.com), David Smiley (dsmiley@mitre.org)
 * @since 1.0
 */
@Mojo( name = "java", threadSafe = true, requiresDependencyResolution = ResolutionScope.TEST )
public class ExecJavaMojo
    extends AbstractExecMojo
{
    @Component
    private ArtifactResolver artifactResolver;

    @Component
    private ArtifactFactory artifactFactory;

    @Component
    private ArtifactMetadataSource metadataSource;

    /**
     * @since 1.0
     */
    @Parameter( readonly = true, required = true, defaultValue = "${localRepository}" )
    private ArtifactRepository localRepository;

    /**
     * @since 1.1-beta-1
     */
    @Parameter( readonly = true, required = true, defaultValue = "${project.remoteArtifactRepositories}" )
    private List<ArtifactRepository> remoteRepositories;

    /**
     * @since 1.0
     */
    @Component
    private MavenProjectBuilder projectBuilder;

    /**
     * @since 1.1-beta-1
     */
    @Parameter( readonly = true, defaultValue = "${plugin.artifacts}" )
    private List<Artifact> pluginDependencies;

    /**
     * The main class to execute.
     * 
     * @since 1.0
     */
    @Parameter( required = true, property = "exec.mainClass" )
    private String mainClass;

    /**
     * The class arguments.
     * 
     * @since 1.0
     */
    @Parameter( property = "exec.arguments" )
    private String[] arguments;

    /**
     * A list of system properties to be passed. Note: as the execution is not forked, some system properties required
     * by the JVM cannot be passed here. Use MAVEN_OPTS or the exec:exec instead. See the user guide for more
     * information.
     * 
     * @since 1.0
     */
    @Parameter
    private Property[] systemProperties;

    /**
     * Indicates if mojo should be kept running after the mainclass terminates. Use full for server like apps with
     * daemon threads.
     * 
     * @deprecated since 1.1-alpha-1
     * @since 1.0
     */
    @Parameter( property = "exec.keepAlive", defaultValue = "false" )
    @Deprecated
    private boolean keepAlive;

    /**
     * Indicates if the project dependencies should be used when executing the main class.
     * 
     * @since 1.1-beta-1
     */
    @Parameter( property = "exec.includeProjectDependencies", defaultValue = "true" )
    private boolean includeProjectDependencies;

    /**
     * Indicates if this plugin's dependencies should be used when executing the main class.
     * <p/>
     * This is useful when project dependencies are not appropriate. Using only the plugin dependencies can be
     * particularly useful when the project is not a java project. For example a mvn project using the csharp plugins
     * only expects to see dotnet libraries as dependencies.
     * 
     * @since 1.1-beta-1
     */
    @Parameter( property = "exec.includePluginsDependencies", defaultValue = "false" )
    private boolean includePluginDependencies;

    /**
     * Whether to interrupt/join and possibly stop the daemon threads upon quitting. <br/>
     * If this is <code>false</code>, maven does nothing about the daemon threads. When maven has no more work to do,
     * the VM will normally terminate any remaining daemon threads.
     * <p>
     * In certain cases (in particular if maven is embedded), you might need to keep this enabled to make sure threads
     * are properly cleaned up to ensure they don't interfere with subsequent activity. In that case, see
     * {@link #daemonThreadJoinTimeout} and {@link #stopUnresponsiveDaemonThreads} for further tuning.
     * </p>
     * 
     * @since 1.1-beta-1
     */
    @Parameter( property = "exec.cleanupDaemonThreads", defaultValue = "true" )
    private boolean cleanupDaemonThreads;

    /**
     * This defines the number of milliseconds to wait for daemon threads to quit following their interruption.<br/>
     * This is only taken into account if {@link #cleanupDaemonThreads} is <code>true</code>. A value &lt;=0 means to
     * not timeout (i.e. wait indefinitely for threads to finish). Following a timeout, a warning will be logged.
     * <p>
     * Note: properly coded threads <i>should</i> terminate upon interruption but some threads may prove problematic: as
     * the VM does interrupt daemon threads, some code may not have been written to handle interruption properly. For
     * example java.util.Timer is known to not handle interruptions in JDK &lt;= 1.6. So it is not possible for us to
     * infinitely wait by default otherwise maven could hang. A sensible default value has been chosen, but this default
     * value <i>may change</i> in the future based on user feedback.
     * </p>
     * 
     * @since 1.1-beta-1
     */
    @Parameter( property = "exec.daemonThreadJoinTimeout", defaultValue = "15000" )
    private long daemonThreadJoinTimeout;

    /**
     * Wether to call {@link Thread#stop()} following a timing out of waiting for an interrupted thread to finish. This
     * is only taken into account if {@link #cleanupDaemonThreads} is <code>true</code> and the
     * {@link #daemonThreadJoinTimeout} threshold has been reached for an uncooperative thread. If this is
     * <code>false</code>, or if {@link Thread#stop()} fails to get the thread to stop, then a warning is logged and
     * Maven will continue on while the affected threads (and related objects in memory) linger on. Consider setting
     * this to <code>true</code> if you are invoking problematic code that you can't fix. An example is
     * {@link java.util.Timer} which doesn't respond to interruption. To have <code>Timer</code> fixed, vote for
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6336543">this bug</a>.
     * 
     * @since 1.1-beta-1
     */
    @Parameter( property = "exec.stopUnresponsiveDaemonThreads", defaultValue = "false" )
    private boolean stopUnresponsiveDaemonThreads;

    /**
     * Deprecated this is not needed anymore.
     * 
     * @deprecated since 1.1-alpha-1
     * @since 1.0
     */
    @Parameter( property = "exec.killAfter", defaultValue = "-1" )
    @Deprecated
    private long killAfter;

    private Properties originalSystemProperties;

    /**
     * Additional elements to be appended to the classpath.
     * 
     * @since 1.3
     */
    @Parameter
    private List<String> additionalClasspathElements;

    /**
     * Execute goal.
     * 
     * @throws MojoExecutionException execution of the main class or one of the threads it generated failed.
     * @throws MojoFailureException something bad happened...
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( isSkip() )
        {
            getLog().info( "skipping execute as per configuration" );
            return;
        }
        if ( killAfter != -1 )
        {
            getLog().warn( "Warning: killAfter is now deprecated. Do you need it ? Please comment on MEXEC-6." );
        }

        if ( null == arguments )
        {
            arguments = new String[0];
        }

        if ( getLog().isDebugEnabled() )
        {
            StringBuffer msg = new StringBuffer( "Invoking : " );
            msg.append( mainClass );
            msg.append( ".main(" );
            for ( int i = 0; i < arguments.length; i++ )
            {
                if ( i > 0 )
                {
                    msg.append( ", " );
                }
                msg.append( arguments[i] );
            }
            msg.append( ")" );
            getLog().debug( msg );
        }

        IsolatedThreadGroup threadGroup = new IsolatedThreadGroup( mainClass /* name */ );
        Thread bootstrapThread = new Thread( threadGroup, new Runnable()
        {
            public void run()
            {
                try
                {
                    Method main =
                        Thread.currentThread().getContextClassLoader().loadClass( mainClass ).getMethod( "main",
                                                                                                         new Class[] {
                                                                                                             String[].class } );
                    if ( !main.isAccessible() )
                    {
                        getLog().debug( "Setting accessibility to true in order to invoke main()." );
                        main.setAccessible( true );
                    }
                    if ( !Modifier.isStatic( main.getModifiers() ) )
                    {
                        throw new MojoExecutionException( "Can't call main(String[])-method because it is not static." );
                    }
                    main.invoke( null, new Object[] { arguments } );
                }
                catch ( NoSuchMethodException e )
                { // just pass it on
                    Thread.currentThread().getThreadGroup().uncaughtException( Thread.currentThread(),
                                                                               new Exception( "The specified mainClass doesn't contain a main method with appropriate signature.",
                                                                                              e ) );
                }
                catch ( InvocationTargetException e )
                { // use the cause if available to improve the plugin execution output
                   Throwable exceptionToReport = e.getCause() != null ? e.getCause() : e;
                   Thread.currentThread().getThreadGroup().uncaughtException( Thread.currentThread(), exceptionToReport );
                }
                catch ( Exception e )
                { // just pass it on
                    Thread.currentThread().getThreadGroup().uncaughtException( Thread.currentThread(), e );
                }
            }
        }, mainClass + ".main()" );
        bootstrapThread.setContextClassLoader( getClassLoader() );
        setSystemProperties();

        bootstrapThread.start();
        joinNonDaemonThreads( threadGroup );
        // It's plausible that spontaneously a non-daemon thread might be created as we try and shut down,
        // but it's too late since the termination condition (only daemon threads) has been triggered.
        if ( keepAlive )
        {
            getLog().warn( "Warning: keepAlive is now deprecated and obsolete. Do you need it? Please comment on MEXEC-6." );
            waitFor( 0 );
        }

        if ( cleanupDaemonThreads )
        {

            terminateThreads( threadGroup );

            try
            {
                threadGroup.destroy();
            }
            catch ( IllegalThreadStateException e )
            {
                getLog().warn( "Couldn't destroy threadgroup " + threadGroup, e );
            }
        }

        if ( originalSystemProperties != null )
        {
            System.setProperties( originalSystemProperties );
        }

        synchronized ( threadGroup )
        {
            if ( threadGroup.uncaughtException != null )
            {
                throw new MojoExecutionException( "An exception occured while executing the Java class. "
                    + threadGroup.uncaughtException.getMessage(), threadGroup.uncaughtException );
            }
        }

        registerSourceRoots();
    }

    /**
     * a ThreadGroup to isolate execution and collect exceptions.
     */
    class IsolatedThreadGroup
        extends ThreadGroup
    {
        private Throwable uncaughtException; // synchronize access to this

        public IsolatedThreadGroup( String name )
        {
            super( name );
        }

        public void uncaughtException( Thread thread, Throwable throwable )
        {
            if ( throwable instanceof ThreadDeath )
            {
                return; // harmless
            }
            synchronized ( this )
            {
                if ( uncaughtException == null ) // only remember the first one
                {
                    uncaughtException = throwable; // will be reported eventually
                }
            }
            getLog().warn( throwable );
        }
    }

    private void joinNonDaemonThreads( ThreadGroup threadGroup )
    {
        boolean foundNonDaemon;
        do
        {
            foundNonDaemon = false;
            Collection<Thread> threads = getActiveThreads( threadGroup );
            for ( Thread thread : threads )
            {
                if ( thread.isDaemon() )
                {
                    continue;
                }
                foundNonDaemon = true; // try again; maybe more threads were created while we were busy
                joinThread( thread, 0 );
            }
        }
        while ( foundNonDaemon );
    }

    private void joinThread( Thread thread, long timeoutMsecs )
    {
        try
        {
            getLog().debug( "joining on thread " + thread );
            thread.join( timeoutMsecs );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt(); // good practice if don't throw
            getLog().warn( "interrupted while joining against thread " + thread, e ); // not expected!
        }
        if ( thread.isAlive() ) // generally abnormal
        {
            getLog().warn( "thread " + thread + " was interrupted but is still alive after waiting at least "
                + timeoutMsecs + "msecs" );
        }
    }

    private void terminateThreads( ThreadGroup threadGroup )
    {
        long startTime = System.currentTimeMillis();
        Set<Thread> uncooperativeThreads = new HashSet<Thread>(); // these were not responsive to interruption
        for ( Collection<Thread> threads = getActiveThreads( threadGroup ); !threads.isEmpty(); threads =
            getActiveThreads( threadGroup ), threads.removeAll( uncooperativeThreads ) )
        {
            // Interrupt all threads we know about as of this instant (harmless if spuriously went dead (! isAlive())
            // or if something else interrupted it ( isInterrupted() ).
            for ( Thread thread : threads )
            {
                getLog().debug( "interrupting thread " + thread );
                thread.interrupt();
            }
            // Now join with a timeout and call stop() (assuming flags are set right)
            for ( Thread thread : threads )
            {
                if ( !thread.isAlive() )
                {
                    continue; // and, presumably it won't show up in getActiveThreads() next iteration
                }
                if ( daemonThreadJoinTimeout <= 0 )
                {
                    joinThread( thread, 0 ); // waits until not alive; no timeout
                    continue;
                }
                long timeout = daemonThreadJoinTimeout - ( System.currentTimeMillis() - startTime );
                if ( timeout > 0 )
                {
                    joinThread( thread, timeout );
                }
                if ( !thread.isAlive() )
                {
                    continue;
                }
                uncooperativeThreads.add( thread ); // ensure we don't process again
                if ( stopUnresponsiveDaemonThreads )
                {
                    getLog().warn( "thread " + thread + " will be Thread.stop()'ed" );
                    thread.stop();
                }
                else
                {
                    getLog().warn( "thread " + thread + " will linger despite being asked to die via interruption" );
                }
            }
        }
        if ( !uncooperativeThreads.isEmpty() )
        {
            getLog().warn( "NOTE: " + uncooperativeThreads.size() + " thread(s) did not finish despite being asked to "
                + " via interruption. This is not a problem with exec:java, it is a problem with the running code."
                + " Although not serious, it should be remedied." );
        }
        else
        {
            int activeCount = threadGroup.activeCount();
            if ( activeCount != 0 )
            {
                // TODO this may be nothing; continue on anyway; perhaps don't even log in future
                Thread[] threadsArray = new Thread[1];
                threadGroup.enumerate( threadsArray );
                getLog().debug( "strange; " + activeCount + " thread(s) still active in the group " + threadGroup
                    + " such as " + threadsArray[0] );
            }
        }
    }

    private Collection<Thread> getActiveThreads( ThreadGroup threadGroup )
    {
        Thread[] threads = new Thread[threadGroup.activeCount()];
        int numThreads = threadGroup.enumerate( threads );
        Collection<Thread> result = new ArrayList<Thread>( numThreads );
        for ( int i = 0; i < threads.length && threads[i] != null; i++ )
        {
            result.add( threads[i] );
        }
        return result; // note: result should be modifiable
    }

    /**
     * Pass any given system properties to the java system properties.
     */
    private void setSystemProperties()
    {
        if ( systemProperties != null )
        {
            originalSystemProperties = System.getProperties();
            for ( Property systemProperty : systemProperties )
            {
                String value = systemProperty.getValue();
                System.setProperty( systemProperty.getKey(), value == null ? "" : value );
            }
        }
    }

    /**
     * Set up a classloader for the execution of the main class.
     * 
     * @return the classloader
     * @throws MojoExecutionException if a problem happens
     */
    private ClassLoader getClassLoader()
        throws MojoExecutionException
    {
        List<URL> classpathURLs = new ArrayList<URL>();
        this.addRelevantPluginDependenciesToClasspath( classpathURLs );
        this.addRelevantProjectDependenciesToClasspath( classpathURLs );
        this.addAdditionalClasspathElements( classpathURLs );
        return new URLClassLoader( classpathURLs.toArray( new URL[classpathURLs.size()] ) );
    }

    private void addAdditionalClasspathElements( List<URL> path )
    {
        if ( additionalClasspathElements != null )
        {
            for ( String classPathElement : additionalClasspathElements )
            {
                try
                {
                    File file = new File( classPathElement );
                    if ( !file.isAbsolute() )
                    {
                        file = new File( project.getBasedir(), classPathElement );
                    }
                    URL url = file.toURI().toURL();
                    getLog().debug( "Adding additional classpath element: " + url + " to classpath" );
                    path.add( url );
                }
                catch ( MalformedURLException e )
                {
                    getLog().warn( "Skipping additional classpath element: " + classPathElement, e );
                }
            }
        }
    }

    /**
     * Add any relevant project dependencies to the classpath. Indirectly takes includePluginDependencies and
     * ExecutableDependency into consideration.
     * 
     * @param path classpath of {@link java.net.URL} objects
     * @throws MojoExecutionException if a problem happens
     */
    private void addRelevantPluginDependenciesToClasspath( List<URL> path )
        throws MojoExecutionException
    {
        if ( hasCommandlineArgs() )
        {
            arguments = parseCommandlineArgs();
        }

        try
        {
            for ( Artifact classPathElement : this.determineRelevantPluginDependencies() )
            {
                getLog().debug( "Adding plugin dependency artifact: " + classPathElement.getArtifactId()
                    + " to classpath" );
                path.add( classPathElement.getFile().toURI().toURL() );
            }
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( "Error during setting up classpath", e );
        }

    }

    /**
     * Add any relevant project dependencies to the classpath. Takes includeProjectDependencies into consideration.
     * 
     * @param path classpath of {@link java.net.URL} objects
     * @throws MojoExecutionException if a problem happens
     */
    private void addRelevantProjectDependenciesToClasspath( List<URL> path )
        throws MojoExecutionException
    {
        if ( this.includeProjectDependencies )
        {
            try
            {
                getLog().debug( "Project Dependencies will be included." );

                List<Artifact> artifacts = new ArrayList<Artifact>();
                List<File> theClasspathFiles = new ArrayList<File>();

                collectProjectArtifactsAndClasspath( artifacts, theClasspathFiles );

                for ( File classpathFile : theClasspathFiles )
                {
                    URL url = classpathFile.toURI().toURL();
                    getLog().debug( "Adding to classpath : " + url );
                    path.add( url );
                }

                for ( Artifact classPathElement : artifacts )
                {
                    getLog().debug( "Adding project dependency artifact: " + classPathElement.getArtifactId()
                        + " to classpath" );
                    path.add( classPathElement.getFile().toURI().toURL() );
                }

            }
            catch ( MalformedURLException e )
            {
                throw new MojoExecutionException( "Error during setting up classpath", e );
            }
        }
        else
        {
            getLog().debug( "Project Dependencies will be excluded." );
        }

    }

    /**
     * Determine all plugin dependencies relevant to the executable. Takes includePlugins, and the executableDependency
     * into consideration.
     * 
     * @return a set of Artifact objects. (Empty set is returned if there are no relevant plugin dependencies.)
     * @throws MojoExecutionException if a problem happens resolving the plufin dependencies
     */
    private Set<Artifact> determineRelevantPluginDependencies()
        throws MojoExecutionException
    {
        Set<Artifact> relevantDependencies;
        if ( this.includePluginDependencies )
        {
            if ( this.executableDependency == null )
            {
                getLog().debug( "All Plugin Dependencies will be included." );
                relevantDependencies = new HashSet<Artifact>( this.pluginDependencies );
            }
            else
            {
                getLog().debug( "Selected plugin Dependencies will be included." );
                Artifact executableArtifact = this.findExecutableArtifact();
                Artifact executablePomArtifact = this.getExecutablePomArtifact( executableArtifact );
                relevantDependencies = this.resolveExecutableDependencies( executablePomArtifact );
            }
        }
        else
        {
            relevantDependencies = Collections.emptySet();
            getLog().debug( "Plugin Dependencies will be excluded." );
        }
        return relevantDependencies;
    }

    /**
     * Get the artifact which refers to the POM of the executable artifact.
     * 
     * @param executableArtifact this artifact refers to the actual assembly.
     * @return an artifact which refers to the POM of the executable artifact.
     */
    private Artifact getExecutablePomArtifact( Artifact executableArtifact )
    {
        return this.artifactFactory.createBuildArtifact( executableArtifact.getGroupId(),
                                                         executableArtifact.getArtifactId(),
                                                         executableArtifact.getVersion(), "pom" );
    }

    /**
     * Resolve the executable dependencies for the specified project
     * 
     * @param executablePomArtifact the project's POM
     * @return a set of Artifacts
     * @throws MojoExecutionException if a failure happens
     */
    private Set<Artifact> resolveExecutableDependencies( Artifact executablePomArtifact )
        throws MojoExecutionException
    {

        Set<Artifact> executableDependencies;
        try
        {
            MavenProject executableProject =
                this.projectBuilder.buildFromRepository( executablePomArtifact, this.remoteRepositories,
                                                         this.localRepository );

            // get all of the dependencies for the executable project
            List<Dependency> dependencies = executableProject.getDependencies();

            // make Artifacts of all the dependencies
            Set<Artifact> dependencyArtifacts =
                MavenMetadataSource.createArtifacts( this.artifactFactory, dependencies, null, null, null );

            // not forgetting the Artifact of the project itself
            dependencyArtifacts.add( executableProject.getArtifact() );

            // resolve all dependencies transitively to obtain a comprehensive list of assemblies
            ArtifactResolutionResult result =
                artifactResolver.resolveTransitively( dependencyArtifacts, executablePomArtifact,
                                                      Collections.emptyMap(), this.localRepository,
                                                      this.remoteRepositories, metadataSource, null,
                                                      Collections.emptyList() );
            executableDependencies = result.getArtifacts();
        }
        catch ( Exception ex )
        {
            throw new MojoExecutionException( "Encountered problems resolving dependencies of the executable "
                + "in preparation for its execution.", ex );
        }

        return executableDependencies;
    }

    /**
     * Stop program execution for nn millis.
     * 
     * @param millis the number of millis-seconds to wait for, <code>0</code> stops program forever.
     */
    private void waitFor( long millis )
    {
        Object lock = new Object();
        synchronized ( lock )
        {
            try
            {
                lock.wait( millis );
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt(); // good practice if don't throw
                getLog().warn( "Spuriously interrupted while waiting for " + millis + "ms", e );
            }
        }
    }

}
