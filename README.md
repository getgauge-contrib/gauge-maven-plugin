# gauge-maven-plugin

Maven plugin to run [gauge](http://getgauge.io) specs.

## Prerequisites
* [Gauge](http://getgauge.io) should be installed and in $PATH. Get gauge from the [gauge download page](http://getgauge.io/download.html).
* [Gauge java plugin](https://github.com/getgauge/gauge-java) 0.0.6 or higher.

## Add to project

Add the below snippet to pom.xml

````
 <build>
      <plugins>
          <plugin>
              <groupId>com.thoughtworks.gauge.maven</groupId>
              <artifactId>gauge-maven-plugin</artifactId>
              <version>1.0.0</version>
          </plugin>
      </plugins>
  </build>

````

## Executing specs
Run the below command to execute specs
````
mvn gauge:execute -DspecsDir=specs
```
###Execute specs In parallel
```
mvn gauge:execute -DspecsDir=specs -DinParallel=true
```
###Execute specs by [tags expression](http://getgauge.io/documentation/user/current/execution/tagged_execution.html)
```
mvn gauge:execute -DspecsDir=specs -Dtags="!in-progress"
```
### Specifying [execution environment](http://getgauge.io/documentation/user/current/managing_environments/README.html#executing-with-environment)
```
mvn gauge:execute -DspecsDir=specs -Denv="dev"
```

### As a part of maven test phase
Run gauge specs in project as a part of maven test phase by adding the below execution to yor pom.xml

````
 <build>
      <plugins>
          <plugin>
              <groupId>com.thoughtworks.gauge.maven</groupId>
              <artifactId>gauge-maven-plugin</artifactId>
              <version>1.0.0</version>
              <executions>
                  <execution>
                      <phase>test</phase>
                      <configuration>
                          <specsDir>specs</specsDir>
                      </configuration>
                      <goals>
                          <goal>execute</goal>
                      </goals>
                  </execution>
              </executions>
          </plugin>
        </plugins>
  </build>
````
```mvn test``` command will also run gauge specs if the above mentioned execution is added to the projects pom.xml

### All Properties
The following plugin properties can be additionally set:

|Property name|Usage|Description|
|-------------|-----|-----------|
|specsDir| -DspecsDir=specs| Gauge specs directory path. Required for executing specs|
|tags    | -Dtags=tag1 & tag2 |Filter specs by specified tags expression|
|inParallel| -DinParallel=true | Execute specs in parallel|
|nodes    | -Dnodes=3 | Number of parallel execution streams. Use with ```parallel```|
|env      | -Denv=qa  | gauge env to run against  |
|additionalFlags| -DadditionalFlags="--verbose" | Add additional gauge flags to execution|

## License

![GNU Public License version 3.0](http://www.gnu.org/graphics/gplv3-127x51.png)
Gauge is released under [GNU Public License version 3.0](http://www.gnu.org/licenses/gpl-3.0.txt)

## Copyright

Copyright 2015 ThoughtWorks, Inc.


