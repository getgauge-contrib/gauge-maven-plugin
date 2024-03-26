// Copyright 2018 ThoughtWorks, Inc.

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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.gauge.maven.GaugeCommand.GAUGE;
import static com.thoughtworks.gauge.maven.GaugeCommand.VALIDATE;

/**
 * Goal which validates gauge specs in the project.
 */

@Mojo(name = GaugeValidationMojo.GAUGE_VALIDATION_MOJO_NAME, requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.TEST_COMPILE)
public class GaugeValidationMojo extends AbstractMojo {

    public static final String GAUGE_VALIDATION_MOJO_NAME = "validate";

    /**
     * Gauge spec directory path.
     */
    @Parameter(property = "specsDir", required = false)
    private String specsDir;

    /**
     * Gauge working directory path.
     */
    @Parameter(defaultValue = "${project.basedir}", property = "dir", required = false, readonly = true)
    private File dir;

    /**
     * Get Project classpath elements
     */
    @Parameter(property = "project.testClasspathElements", required = true, readonly = true)
    private List<String> classpath;

    /**
     * Gauge environment to validate specs against
     */
    @Parameter(defaultValue = "${gauge.exec.env}", property = "env", required = false)
    private String env;

    /**
     * Additional flags for gauge execution
     */
    @Parameter(defaultValue = "${gauge.exec.additionalFlags}", property = "flags", required = false)
    private List flags;

    /**
     * Additional environment variables to set on the command line.
     */
    @Parameter(property = "environmentVariables", readonly = true)
    private final Map<String, String> environmentVariables = new HashMap<>();

    /** {@inheritDoc} */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            GaugeCommand.execute(getEnvironmentVariables(), getClassPath(), getCommand());
        } catch (GaugeExecutionFailedException e) {
            throw new MojoFailureException("Gauge Specs validation failed. " + e.getMessage(), e);
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing specs. " + e.getMessage(), e);
        }
    }

    public ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add(GAUGE);
        command.add(VALIDATE);
        addEnv(command);
        addAdditionalFlags(command);
        addSpecsDir(command);
        return command;
    }

    private void addSpecsDir(ArrayList<String> command) {
        if (getSpecsDir() != null && getSpecsDir().trim().length() > 0) {
            for (String s : getSpecsDir().split(",")) {
                command.add(GaugeCommand.getSpecsPath(dir, s));
            }
        }
    }

    private void addEnv(ArrayList<String> command) {
        if (this.env != null && this.env.trim().length() > 0) {
            command.add(GaugeCommand.ENV_FLAG);
            command.add(env);
        }
    }

    private void addAdditionalFlags(ArrayList<String> command) {
        if (flags != null) {
            command.addAll(flags);
        }
    }

    public String getSpecsDir() {
        return specsDir;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public List<String> getClassPath() {
        return classpath;
    }

}
