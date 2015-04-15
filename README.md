# gauge-maven-plugin

Maven plugin to run [gauge](http://getgauge.io) specs.

Requires gauge to be installed and in $PATH. Get gauge from the [gauge download page](getgauge.io/download.html).

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

### As a part of spec test phase
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

## License

![GNU Public License version 3.0](http://www.gnu.org/graphics/gplv3-127x51.png)
Gauge is released under [GNU Public License version 3.0](http://www.gnu.org/licenses/gpl-3.0.txt)

## Copyright

Copyright 2015 ThoughtWorks, Inc.


