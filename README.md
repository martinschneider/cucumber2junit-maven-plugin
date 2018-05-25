[<img src="https://travis-ci.com/martinschneider/cucumber2junit.svg?branch=master" height="41" alt="Build status"/>](https://travis-ci.com/martinschneider/cucumber2junit)
[<img src="https://www.buymeacoffee.com/assets/img/guidelines/download-assets-sm-1.svg" height="41" alt="Buy me a coffee"/>](https://www.buymeacoffee.com/mschneider)

# cucumber2junit-maven-plugin

## Background
There are various cloud services which allow running automated tests on simulated or real mobile devices. One of them is [AWS Device Farm](https://aws.amazon.com/device-farm/). Their solution requires uploading the whole test code including all dependencies. This reduces the execution time but also limits the flexibility of how to execute tests.

One major drawback is that **when using Appium and JUnit there is no support for custom test runners**. However, a custom runner (namely `cucumber.api.junit.Cucumber`or an extension of it) is the default way to run Cucumber features with JUnit. Until AWS officially supports Cucumber  there is no straightforward way to run them on their service.

## Solution
This Maven plugin offers a workaround. It parses the feature files in a Java project and generates plain JUnit test classes which can be run within AWS Device Farm.

## Configuration

Add the following  configuration in the `pom.xml` of your project:

    <profiles>
      <profile>
        <id>aws</id>
        <build>
          <plugins> 
            <plugin>
              <groupId>io.github.martinschneider</groupId>
              <artifactId>cucumber2junit-maven-plugin</artifactId>
              <version>1.1</version>
              <executions>
                <execution>
                  <id>aws</id>
                  <phase>generate-sources</phase>
                  <goals>
                    <goal>generate</goal>
                  </goals>
                  <configuration>
                    <packageName>io.github.martinschneider.demo.tests</packageName>
                    <stepsPackageName>io.github.martinschneider.demo.steps</stepsPackageName>
                  </configuration>
                </execution>
              </executions>
            </plugin>
          </plugins>
        </build>
      </profile>
    </profiles>

`packageName` and `stepsPackageName` are the only mandatory parameters but in almost all cases you want to overwrite the default template too:

    <templatePath>/path/to/custom/template.vm</templatePath>

This is the path to a velocity template which will be used for generating the JUnit test code. See [junitTestClass.vm](src/main/resources/junitTestClass.vm) for the default.

Here are the other possible configuration parameter with their respective default values:

    <featuresSourceDirectory>${project.basedir}/src/test/resources/features</featuresSourceDirectory>
    <javaOutputDirectory>${project.basedir}/src/test/java</javaOutputDirectory>
    <featuresClasspath>features</featuresClasspath>
    <featuresOutputDirectory>"/tmp/features"</featuresOutputDirectory>

Make sure that the `featuresOutputDirectory` points to a directory that you are allowed to write to in your AWS setup.

Also, add the configuration detailed in the [AWS documentation](https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-android-appium-java-junit.html#test-types-android-appium-java-junit-prepare) to package your project and all dependencies into a single zip-file.

## Usage
Then simply run `mvn -Paws package -DskipTests=true`. This will result in a zip-file which can be used to run your tests within the AWS Device Farm.

## Additional documentation
### Customising the Velocity template
This plugin uses Apache Velocity to generate Java source code. You can customise the template to the specific needs of your project. For example you might to execute custom code before every test execution.

You can use the following placeholders within the template:

* `className` the name of the test class (derived from the name of the feature)
* `packageName` the package for the JUnit tests
* `featuresSourceDirectory` the absolute path to the feature files
* `javaOutputDirectory` the absolute path to write the JUnit tests to
* `feature` the feature object (see ... for the public methods it exposes)
* `featuresOutputDirectory` the absolute path to write the feature files to
* `featuresClasspath`, the classpath to load the feature files from
* `scenarios` the scenarios (see ... for the Java code)
* `stepsPackage` the package including the glue code (steps)

The (absolute) path to the template file can be set in the configuration of the plugin.
 
    <templatePath>/path/to/custom/template.vm</templatePath>

If it is not specified the [default](src/main/resources/junitTestClass.vm) will be used.

### Additional placeholders
You can also define a map of additional placeholders for the velocity template:

    <configuration>
      ...
      <additionalPlaceholders>
       <key1>value1</key1>
       <key2>value2</key2>
      </additionalPlaceholders>
      ...
    </configuration>

Maven resolves system properties within the values so the following example works as well:

    <additionalPlaceholders>
     <key>${someSystemProp}</key>
    </additionalPlaceholders>

You can then pass the value as a parameter when packaging your project: `mvn -Paws -DsomeSystemProp=value package -DskipTests=true`.

All occurences of `$key` will then be replaced with `value` when processing the template..


## Contact
Martin Schneider, mart.schneider@gmail.com

[![Buy me a coffee](https://www.buymeacoffee.com/assets/img/guidelines/download-assets-1.svg)](https://www.buymeacoffee.com/mschneider)