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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which executes gauge specs in the project
 */

@Mojo(name = GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.TEST)
public class GaugeExecutionMojo extends AbstractMojo {

    public static final String GAUGE_EXEC_MOJO_NAME = "execute";

    /**
     * Gauge spec directory path.
     */
    @Parameter(property = "specsDir", required = false)
    private String specsDir;

    /**
     * Gauge working directory path.
     */
    @Parameter(defaultValue = "${project.basedir}", property = "dir", required = false)
    private File dir;

    /**
     * Tags to execute. An expression eg. tag1 & tag2 & !tag3
     */
    @Parameter(defaultValue = "${gauge.exec.tags}", property = "tags", required = false)
    private String tags;

    /**
     * Scenario to execute.
     */
    @Parameter(property = "scenario", required = false)
    private String scenario;

    /**
     * Scenario to execute.
     */
    @Parameter(property = "tableRows", required = false)
    private String tableRows;

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

    /**
     * Set to true to skip running tests, but still compile them.
     */
    @Parameter(property = "skipTests", defaultValue = "false")
    private boolean skipTests;

    /**
     * Set to true to bypass tests. If you enable "maven.test.skip" property,
     * "maven.test.skip" property disables both running the tests and compiling the tests.
     */
    @Parameter(property = "maven.test.skip", defaultValue = "false")
    private boolean skip;

    /** {@inheritDoc} */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!isGaugeProject() || !verifyParameters()) {
            return;
        }
        try {
            GaugeCommand.execute(classpath, getCommand());
        } catch (GaugeExecutionFailedException e) {
            throw new MojoFailureException("Gauge Specs execution failed. " + e.getMessage(), e);
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing specs. " + e.getMessage(), e);
        }

    }

    private boolean isGaugeProject() {
        return Files.exists(Paths.get(dir.getAbsolutePath(), "manifest.json"));
    }

    public boolean verifyParameters() {
        if (isSkipTests() || isSkip()) {
            getLog().info("------------------------------------------------------------------------");
            getLog().info("Tests are skipped. ");
            getLog().info("------------------------------------------------------------------------");

            return false;
        }
        return true;
    }

    public ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add(GaugeCommand.GAUGE);
        command.add(GaugeCommand.RUN);
        if (hasRepeatFlag() || hasFailedFlag()) return withRepeatOrFailed(command);
        addTags(command);
        addScenario(command);
        addParallelFlags(command);
        addEnv(command);
        addAdditionalFlags(command);
        addDir(command);
        addSpecsDir(command);
        addTableRows(command);
        return command;
    }

    private ArrayList<String> withRepeatOrFailed(ArrayList<String> command) {
        if (hasRepeatFlag()) command.add(GaugeCommand.REPEAT_FLAG);
        if (hasFailedFlag()) command.add(GaugeCommand.FAILED_FLAG);
        return command;
    }

    private boolean hasFailedFlag() {
        return this.flags != null && !this.flags.isEmpty() && this.flags.contains(GaugeCommand.FAILED_FLAG);
    }

    private boolean hasRepeatFlag() {
        return this.flags != null && !this.flags.isEmpty() && this.flags.contains(GaugeCommand.REPEAT_FLAG);
    }

    private void addTableRows(ArrayList<String> command) {
        if (this.tableRows != null && this.tableRows.trim().length() > 0) {
            command.add(GaugeCommand.TABLEROWS_FLAG);
            command.add(tableRows);
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

    private void addParallelFlags(ArrayList<String> command) {
        if (inParallel != null && inParallel) {
            command.add(GaugeCommand.PARALLEL_FLAG);
            if (nodes != 0) {
                command.add(GaugeCommand.NODES_FLAG);
                command.add(Integer.toString(nodes));
            }
        }
    }

    private void addDir(ArrayList<String> command) {
        if (this.dir != null) {
            command.add(GaugeCommand.DIR_FLAG + this.dir.getAbsolutePath());
        }
    }

    private void addSpecsDir(ArrayList<String> command) {
        if (this.specsDir != null) {
            for (String s : specsDir.split(",")) {
                command.add(GaugeCommand.getSpecsPath(dir, s));
            }
        }
    }

    private void addTags(ArrayList<String> command) {
        if (this.tags != null && this.tags.trim().length() > 0) {
            command.add(GaugeCommand.TAGS_FLAG);
            command.add(tags);
        }
    }

    private void addScenario(ArrayList<String> command) {
        if (this.scenario != null && this.scenario.trim().length() > 0) {
            command.add(GaugeCommand.SCENARIO_FLAG);
            command.add(scenario);
        }
    }

    private void warn(String format, String... args) {
        getLog().warn("[gauge] " + String.format(format, args));
    }

    private void debug(String format, String... args) {
        getLog().debug("[gauge] " + String.format(format, args));
    }

    public String getSpecsDir() {
        return specsDir;
    }

    public String getTableRows() {
        return tableRows;
    }

    public String getTags() {
        return tags;
    }

    public String getScenario() {
        return scenario;
    }

    public boolean isSkipTests() {
        return skipTests;
    }

    public boolean isSkip() {
        return skip;
    }
}
