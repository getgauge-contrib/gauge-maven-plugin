package com.thoughtworks.gauge.maven;

import com.thoughtworks.gauge.maven.exception.GaugeExecutionFailedException;
import com.thoughtworks.gauge.maven.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GaugeCommand {

    static final String DIR_FLAG = "--dir=";
    static final String TAGS_FLAG = "--tags";
    static final String SCENARIO_FLAG = "--scenario";
    static final String GAUGE = "gauge";
    static final String RUN = "run";
    static final String VALIDATE = "validate";
    static final String PARALLEL_FLAG = "--parallel";
    static final String NODES_FLAG = "-n";
    static final String GAUGE_CUSTOM_CLASSPATH_ENV = "gauge_custom_classpath";
    static final String ENV_FLAG = "--env";
    static final String TABLEROWS_FLAG = "--table-rows";
    static final String REPEAT_FLAG = "--repeat";
    static final String FAILED_FLAG = "--failed";

    static void execute(final Map<String, String> environmentVariables, final List<String> classpath, final List<String> command) throws GaugeExecutionFailedException {
        try {
            ProcessBuilder builder = createProcessBuilder(environmentVariables, classpath, command);
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

    static ProcessBuilder createProcessBuilder(final Map<String, String> environmentVariables, final List<String> classpath, final List<String> command) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        final String customClasspath = createCustomClasspath(classpath);
        builder.environment().put(GaugeCommand.GAUGE_CUSTOM_CLASSPATH_ENV, customClasspath);
        builder.environment().putAll(
                environmentVariables.entrySet().stream().filter(variable -> Objects.nonNull(variable.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        return builder;
    }

    static String createCustomClasspath(final List<String> classpath) {
        if (classpath == null || classpath.isEmpty()) {
            return "";
        }
        return StringUtils.join(classpath, File.pathSeparator);
    }

    /**
     * Merges the specs path with base dir
     *
     * @param specsDir
     * @return Returns absolute path joining base dir with specsDir
     */
    static String getSpecsPath(final File dir, final String specsDir) {
        return new File(dir, specsDir).getAbsolutePath();
    }

}
