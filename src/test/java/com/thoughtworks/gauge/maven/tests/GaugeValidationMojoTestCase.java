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

package com.thoughtworks.gauge.maven.tests;

import com.thoughtworks.gauge.maven.GaugeValidationMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GaugeValidationMojoTestCase extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testSimpleGaugeMojo() throws Exception {
        File testPom = getPomFile("simple_config.xml");

        GaugeValidationMojo mojo = (GaugeValidationMojo) lookupMojo(GaugeValidationMojo.GAUGE_VALIDATION_MOJO_NAME, testPom);

        assertNotNull(mojo);
        assertNull(mojo.getSpecsDir());
    }

    public void testSimpleGaugeMojoWithSpecsDir() throws Exception {
        File testPom = getPomFile("validate_specs.xml");

        GaugeValidationMojo mojo = (GaugeValidationMojo) lookupMojo(GaugeValidationMojo.GAUGE_VALIDATION_MOJO_NAME, testPom);

        assertNotNull(mojo);
        assertEquals("specs", mojo.getSpecsDir());

        ArrayList<String> actual = mojo.getCommand();
        List<String> expected = Arrays.asList("gauge", "validate", getPath(getBasedir(), "specs"));
        assertEquals(expected, actual);
    }

    public void testGetCommandWithMultipleSpecs() throws Exception {
        File testPom = getPomFile("multiple_specs.xml");

        GaugeValidationMojo mojo = (GaugeValidationMojo) lookupMojo(GaugeValidationMojo.GAUGE_VALIDATION_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.getCommand();
        List<String> expected = Arrays.asList("gauge", "validate", getPath(getBasedir(), "specs/dir1"), getPath(getBasedir(), "specs/dir2"), getPath(getBasedir(),"specifications"));
        assertEquals(expected, actual);
    }

    public void testGerCommandWithHideSuggestion() throws Exception {
        File testPom = getPomFile("validate_flags.xml");

        GaugeValidationMojo mojo = (GaugeValidationMojo) lookupMojo(GaugeValidationMojo.GAUGE_VALIDATION_MOJO_NAME, testPom);

        ArrayList<String> actual = mojo.getCommand();
        List<String> expected = Arrays.asList("gauge", "validate", "--hide-suggestion", getPath(getBasedir(), "specs"));
        assertEquals(expected, actual);
    }

    private String getPath(String baseDir, String fileName) {
        return new File(baseDir, fileName).getAbsolutePath();
    }

    private File getPomFile(String fileName) {
        return new File(getBasedir(), "src/test/resources/poms/" + fileName);
    }
}
