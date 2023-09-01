# gauge-maven-plugin

[![Actions Status](https://github.com/getgauge/gauge-maven-plugin/workflows/Build/badge.svg)](https://github.com/getgauge/gauge-maven-plugin/actions)
[![Maven Central](https://img.shields.io/maven-central/v/com.thoughtworks.gauge.maven/gauge-maven-plugin.svg?maxAge=300)](http://search.maven.org/#search|ga|1|g:"com.thoughtworks.gauge.maven")

This plugin is used to integrate gauge with maven so that the specs can be run using maven.

Maven plugin to run [Gauge](https://gauge.org/) specs.

## Prerequisites

* [Gauge](https://gauge.org/) should be installed and in $PATH (0.9.0 or higher version). Latest version of Gauge can be downloaded using these [instructions](https://docs.gauge.org/getting_started/installing-gauge.html).
* [Gauge Java plugin](https://github.com/getgauge/gauge-java) 0.6.0 or higher.

## Create project from archetype

[Gauge-mvn-archetypes](https://github.com/getgauge/gauge-mvn-archetypes) provide templates to create gauge-maven projects.

## Add to project

Add the below snippet to pom.xml

```
<build>
     <plugins>
         <plugin>
             <groupId>com.thoughtworks.gauge.maven</groupId>
             <artifactId>gauge-maven-plugin</artifactId>
             <version>1.4.0</version>
         </plugin>
     </plugins>
 </build>
```

## Executing specs

Run the below command to execute all specifications in `specs` directory

```
mvn gauge:execute -DspecsDir=specs
```

Run the below command to execute a single specification

```
mvn gauge:execute -DspecsDir=specs/example.spec
```

Run the below command to execute specifications in `specs` and `specDir` directories

```
mvn gauge:execute -DspecsDir="specs,specDir"
```

Run the below command to execute the failed scenarios

```
mvn gauge:execute -Dflags="--failed"
```

Run the below command to execute the repeat scenarios

```
mvn gauge:execute -Dflags="--repeat"
```

**Note**
`mvn test-compile` should be used for the tool to get latest changes of user code.

### Execute specs In parallel

```
mvn gauge:execute -DspecsDir=specs -DinParallel=true
```

### Execute specs by tags expression

```
mvn gauge:execute -DspecsDir=specs -Dtags="!in-progress"
```

### Execute spec by scenario name

```
mvn gauge:execute -DspecsDir=specs -Dscenario="Scenario Name"
```

### Specifying execution environment

```
mvn gauge:execute -DspecsDir=specs -Denv="dev"
```

### As a part of maven test phase

Run gauge specs in project as a part of maven test phase by adding the below execution to yor pom.xml

```
<build>
     <plugins>
         <plugin>
             <groupId>com.thoughtworks.gauge.maven</groupId>
             <artifactId>gauge-maven-plugin</artifactId>
             <version>1.6.0</version>
             <executions>
                 <execution>
                     <phase>test</phase>
                     <configuration>
                         <specsDir>specs</specsDir>
                         <environmentVariables>
                            <CUSTOM_ENV_VARIABLE>value</CUSTOM_ENV_VARIABLE>
                         </environmentVariables>
                     </configuration>
                     <goals>
                         <goal>execute</goal>
                     </goals>
                 </execution>
             </executions>
         </plugin>
       </plugins>
 </build>
```

`mvn test` command will also run gauge specs if the above mentioned execution is added to the projects pom.xml

### Validate specs in project

Run the below command to execute all specifications in `specs` directory

```bash
mvn gauge:validate -DspecsDir=specs
```

Run the below command to validate and ignore stub implementation suggestions

```
mvn gauge:validate -Dflags="--hide-suggestion"
```

### As a part of maven test-compile phase

Validate gauge specs in project as a part of maven test-compile phase by adding the below execution to yor pom.xml

```
<build>
     <plugins>
         <plugin>
             <groupId>com.thoughtworks.gauge.maven</groupId>
             <artifactId>gauge-maven-plugin</artifactId>
             <version>1.6.0</version>
             <executions>
                 <execution>
                     <phase>test-compile</phase>
                     <configuration>
                         <specsDir>specs</specsDir>
                         <flags>
                            <flag>--hide-suggestion</flag>
                         </flags>
                     </configuration>
                     <goals>
                         <goal>validate</goal>
                     </goals>
                 </execution>
             </executions>
         </plugin>
       </plugins>
 </build>
```

### Running both goals (validate and execute) as part of maven

Add the following execution to pom.xml to run both goals:

```bash
<plugin>
    <groupId>com.thoughtworks.gauge.maven</groupId>
    <artifactId>gauge-maven-plugin</artifactId>
    <version>1.6.0</version>
    <executions>
        <execution>
            <id>validate</id>
            <phase>test-compile</phase>
            <goals>
                <goal>validate</goal>
            </goals>
        </execution>
        <execution>
            <id>execute</id>
            <phase>test</phase>
            <goals><goal>execute</goal></goals>
            <configuration>
                <specsDir>specs</specsDir>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### All Properties

The following plugin properties can be additionally set:

|Property name|Usage|Description|
|-------------|-----|-----------|
|specsDir| -DspecsDir=specs | Gauge specs directory path. Required for executing specs. Takes a comma separated list of specification files/directories|
|tags    | -Dtags="tag1 & tag2" |Filter specs by specified tags expression|
|inParallel| -DinParallel=true | Execute specs in parallel|
|nodes    | -Dnodes=3 | Number of parallel execution streams. Use with `parallel`|
|env      | -Denv=qa  | gauge env to run against  |
|flags| -Dflags="--verbose,--simpleConsole" | Add additional gauge flags to execution|

## Docs

* Read the [user docs](https://docs.gauge.org/configuration.html#maven) for more details.
* See [gauge-tests](https://github.com/getgauge/gauge-tests) for project example of usage.

## License

![GNU Public License version 3.0](http://www.gnu.org/graphics/gplv3-127x51.png)
Gauge maven plugin is released under [GNU Public License version 3.0](http://www.gnu.org/licenses/gpl-3.0.txt)

## Copyright

Copyright 2015 ThoughtWorks, Inc.
