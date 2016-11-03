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

package com.thoughtworks.gauge.maven.tests;

import com.thoughtworks.gauge.maven.GaugeExecutionMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GaugeExecutionMojoTestCase extends AbstractMojoTestCase {

    protected void setUp() throws Exception
    {
        super.setUp();
    }


    public void testSimpleGaugeMojo() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/simple_config.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        assertNotNull(mojo);
        assertNull(mojo.getSpecsDir());
        assertNull(mojo.getTags());
    }

    public void testSimpleGaugeMojoWithSpecsDirAndTags() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/specs_tags.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        assertNotNull(mojo);
        assertEquals("specs", mojo.getSpecsDir());
        assertEquals("!in-progress", mojo.getTags());
    }

    public void testGetCommandWithSpecsDirAndTagsSet() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/specs_tags.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.createGaugeCommand();
        List<String> expected = Arrays.asList(new String[]{"gauge", "--tags", "!in-progress", new File(getBasedir(), "specs").getAbsolutePath()});
        assertEquals(expected, actual);
    }

    public void testGetCommandWithMultipleSpecs() throws Exception {
        File testPom = new File( getBasedir(), "src/test/resources/poms/multiple_specs.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.createGaugeCommand();
        List<String> expected = Arrays.asList(new String[]{"gauge", new File(getBasedir(), "specs/dir1").getAbsolutePath(),
                new File(getBasedir(), "specs/dir2").getAbsolutePath(),
                new File(getBasedir(), "specifications").getAbsolutePath()});
        assertEquals(expected, actual);
    }

    public void testGetCommandWithDirSet() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/dir.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.createGaugeCommand();
        String directory = new File("some-directory").getAbsolutePath();
        List<String> expected = Arrays.asList(new String[]{"gauge", "--dir=" + directory, "specs"});
        assertEquals(expected, actual);
    }

    public void testGetCommandWithDirProjectBaseDir() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/simple-config.xml" );

        MavenProject project  = new MavenProject();
        project.setFile(testPom);

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupConfiguredMojo(project, GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME);

        ArrayList<String> actual = mojo.createGaugeCommand();
        String directory = testPom.getParentFile().getAbsolutePath();
        List<String> expected = Arrays.asList(new String[]{"gauge", "--dir=" + directory, "specs"});
        assertEquals(expected, actual);
    }

    public void testGetCommandWithParallelExecution() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/simple_parallel.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.createGaugeCommand();
        List<String> expected = Arrays.asList(new String[]{"gauge", "--parallel", new File(getBasedir(), "specs").getAbsolutePath()});
        assertEquals(expected, actual);
    }

    public void testGetCommandWithTagsAndParallelExecutionWithNumberOfNodes() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/parallel_nodes.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.createGaugeCommand();
        List<String> expected = Arrays.asList(new String[]{"gauge","--tags", "tag1 & tag2 || tag3", "--parallel", "-n", "4", new File(getBasedir(), "specs").getAbsolutePath()});
        assertEquals(expected, actual);
    }

    public void testGetCommandWithTagsParallelAndEnv() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/env_parallel.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.createGaugeCommand();
        List<String> expected = Arrays.asList(new String[]{"gauge","--tags", "!tag1", "--parallel", "--env", "dev", new File(getBasedir(), "specs").getAbsolutePath()});
        assertEquals(expected, actual);
    }

    public void testGetCommandWithAdditionalFlags() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/poms/flags.xml" );

        GaugeExecutionMojo mojo = (GaugeExecutionMojo) lookupMojo(GaugeExecutionMojo.GAUGE_EXEC_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.createGaugeCommand();
        List<String> expected = Arrays.asList(new String[]{"gauge", "--verbose", "--log-level", "debug", new File(getBasedir(), "specs").getAbsolutePath()});
        assertEquals(expected, actual);
    }

}
