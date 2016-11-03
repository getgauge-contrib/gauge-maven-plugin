// Copyright 2015 ThoughtWorks, Inc.

// This file is part of Gauge-maven-plugin.

// Gauge-maven-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// Gauge-maven-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with Gauge-maven-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.maven;

import com.thoughtworks.gauge.maven.exception.GaugeExecutionFailedException;
import com.thoughtworks.gauge.maven.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which executes gauge specs in the project
 *
 */

@Mojo( name = GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.TEST)
public class GaugeExecutionMojo
    extends AbstractMojo
{
    public static final String GAUGE_EXEC_MOJO_NAME = "execute";
    public static final String DIR_FLAG = "--dir=";
    public static final String TAGS_FLAG = "--tags";
    public static final String GAUGE = "gauge";
    public static final String PARALLEL_FLAG = "--parallel";
    public static final String DEFAULT_SPECS_DIR = "specs";
    private static final String NODES_FLAG = "-n";
    public static final String GAUGE_CUSTOM_CLASSPATH_ENV = "gauge_custom_classpath";
    private static final String ENV_FLAG = "--env";

    /**
     * Gauge spec directory path.
     */
    @Parameter( defaultValue = "${gauge.specs.directory}", property = "specsDir", required = false )
    private String specsDir;

    /**
     * Gauge working directory path.
     */
    @Parameter( defaultValue = "${project.basedir}", property = "dir", required = false )
    private File dir;

    /**
     * Tags to execute. An expression eg. tag1 & tag2 & !tag3
     */
    @Parameter(defaultValue = "${gauge.exec.tags}", property = "tags", required = false)
    private String tags;

    /**
     * Set to true to execute specs in parallel
     */
    @Parameter(defaultValue = "${gauge.exec.inParallel}", property = "inParallel", required = false)
    private Boolean inParallel;

    /**
     * Number of parallel execution nodes to run the specs in parallel in. Specify only for parallel execution
     */
    @Parameter(defaultValue = "${gauge.exec.nodes}", property = "nodes", required = false)
    private int nodes;

    /**
     * Gauge environment to run specs against
     */
    @Parameter(defaultValue = "${gauge.exec.env}", property = "env", required = false)
    private String env;

    /**
     * Additional flags for gauge execution
     */
    @Parameter(defaultValue = "${gauge.exec.additionalFlags}", property = "flags", required = false)
    private List flags;

    /**
     * Get Project classpath elements
     */
    @Parameter(property = "project.testClasspathElements", required = true, readonly = true)
    private List<String> classpath;

    public void execute()
            throws MojoExecutionException, MojoFailureException {
        try {
            executeGaugeSpecs();
        } catch (GaugeExecutionFailedException e) {
            throw new MojoFailureException("Gauge Specs execution failed");
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing specs. " + e.getMessage(), e);
        }

    }

    private void executeGaugeSpecs() throws GaugeExecutionFailedException {
        try {
            ProcessBuilder builder = createProcessBuilder();
            debug("Executing => " + builder.command());
            Process process = builder.start();
            Util.InheritIO(process.getInputStream(), System.out);
            Util.InheritIO(process.getErrorStream(), System.err);
            if (process.waitFor() != 0) {
                throw new GaugeExecutionFailedException();
            }
        } catch (InterruptedException e) {
            throw new GaugeExecutionFailedException(e);
        } catch (IOException e) {
            throw new GaugeExecutionFailedException(e);
        }

    }

    private ProcessBuilder createProcessBuilder() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(createGaugeCommand());
        String customClasspath = createCustomClasspath();
        debug("Setting Custom classpath => %s", customClasspath);
        builder.environment().put(GAUGE_CUSTOM_CLASSPATH_ENV, customClasspath);
        return builder;
    }

    private String createCustomClasspath() {
        if (classpath == null || classpath.isEmpty()) {
            return "";
        }
        return StringUtils.join(classpath, File.pathSeparator);
    }

    public ArrayList<String> createGaugeCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add(GAUGE);
        addTags(command);
        addParallelFlags(command);
        addEnv(command);
        addAdditionalFlags(command);
        addDir(command);
        addSpecsDir(command);
        return command;
    }

    private void addEnv(ArrayList<String> command) {
        if(this.env != null && !this.env.isEmpty()) {
            command.add(ENV_FLAG);
            command.add(env);
        }
    }

    private void addAdditionalFlags(ArrayList<String> command) {
        if (flags != null) {
            command.addAll(flags);
        }
    }

    private void addParallelFlags(ArrayList<String> command) {
        if(inParallel != null && inParallel) {
            command.add(PARALLEL_FLAG);
            if(nodes != 0) {
                command.add(NODES_FLAG);
                command.add(Integer.toString(nodes));
            }
        }
    }

    private void addDir(ArrayList<String> command) {
        if (this.dir != null) {
            command.add(DIR_FLAG + this.dir.getAbsolutePath());
        }
    }

    private void addSpecsDir(ArrayList<String> command) {
        if (this.specsDir != null) {
            System.out.println(specsDir);
            for (String s : specsDir.split(" ")) {
                command.add(new File(s).getAbsolutePath());
            }
        } else {
            warn("Property 'specsDir' not set. Using default value => '%s'", DEFAULT_SPECS_DIR);
            command.add(DEFAULT_SPECS_DIR);
        }
    }

    private void addTags(ArrayList<String> command) {
        if(this.tags != null && !this.tags.isEmpty()) {
            command.add(TAGS_FLAG);
            command.add(tags);
        }
    }

    private void warn(String format, String... args) {
        getLog().warn("[gauge] "+ String.format(format, args));
    }

    private void debug(String format, String... args) {
        getLog().debug("[gauge] "+ String.format(format, args));
    }

    public String getSpecsDir() {
        return specsDir;
    }

    public String getTags() {
        return tags;
    }
}
