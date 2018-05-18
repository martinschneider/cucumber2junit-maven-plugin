package io.github.martinschneider.cucumber2junit;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class JUnitTestGeneratorTest {

  private static final String PACKAGE_NAME = "com.test";
  private static final String STEPS_PACKAGE = "com.test.steps";
  private static final String JAVA_OUTPUT_DIRECTORY = System.getProperty("java.io.tmpdir");
  private static final String FEATURES_OUTPUT_DIRECTORY = System.getProperty("java.io.tmpdir");
  private static final String FEATURES_CLASSPATH = "features";
  private static final String FEATURES_SOURCE_DIRECTORY = "features";
  private static final String TEMPLATE_PATH = "/Users/martinschneider/junit.vm";
  private JUnitTestGenerator target = new JUnitTestGenerator();

  @Test
  public void testGeneration() {
    target.generateJUnitTests(
        Stream.of(
                TestDataProvider.getFeature1(),
                TestDataProvider.getFeature2(),
                TestDataProvider.getFeature3())
            .collect(Collectors.toList()),
        FEATURES_SOURCE_DIRECTORY,
        JAVA_OUTPUT_DIRECTORY,
        FEATURES_OUTPUT_DIRECTORY,
        FEATURES_CLASSPATH,
        PACKAGE_NAME,
        STEPS_PACKAGE,
        TEMPLATE_PATH,
        Collections.emptyMap());
  }
}
