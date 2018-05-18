package io.github.martinschneider.cucumber2junit;

import io.github.martinschneider.cucumber2junit.model.Feature;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class generates plain JUnit test code for feature files. Each feature is generated in its
 * own test class, every scenario is one test method in that class.
 */
public class JUnitTestGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(JUnitTestGenerator.class);

  public void generateJUnitTests(
      List<Feature> features,
      String featuresSourceDirectory,
      String javaOutputDirectory,
      String featuresOutputDirectory,
      String featuresClasspath,
      String packageName,
      String stepsPackage,
      String templatePath,
      Map<String, String> additionalPlaceholders) {
    VelocityEngine velocityEngine = new VelocityEngine();
    String template = "junitTestClass.vm";
    velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,classpath");
    if (templatePath != null && !templatePath.isEmpty()) {
      Path path = Paths.get(templatePath);
      velocityEngine.setProperty(
          RuntimeConstants.FILE_RESOURCE_LOADER_PATH, path.getParent().toString());
      template = path.getFileName().toString();
    }
    velocityEngine.setProperty(
        "classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    velocityEngine.init();
    Template velocityTemplate = velocityEngine.getTemplate(template);

    for (Feature feature : features) {
      VelocityContext context = new VelocityContext();
      String className = JavaUtils.toClassName(feature.getName());
      context.put("className", className);
      context.put("packageName", packageName);
      context.put("featuresSourceDirectory", featuresSourceDirectory);
      context.put("javaOutputDirectory", javaOutputDirectory);
      context.put("feature", feature);
      context.put("featuresOutputDirectory", featuresOutputDirectory);
      context.put("featuresClasspath", featuresClasspath);
      context.put("scenarios", feature.getScenarios());
      context.put("stepsPackage", stepsPackage);
      for (Map.Entry<String, String> entry : additionalPlaceholders.entrySet()) {
        context.put(entry.getKey(), entry.getValue());
      }
      File outputFile =
          new File(
              javaOutputDirectory
                  + "/"
                  + packageName.replaceAll("\\.", "\\/")
                  + "/"
                  + className
                  + ".java");
      LOG.info("Writing JUnit test class for feature file {} to {}", feature.getName(), outputFile);
      outputFile.getParentFile().mkdirs();
      try (FileWriter writer = new FileWriter(outputFile)) {
        velocityTemplate.merge(context, writer);
        writer.flush();
      } catch (IOException e) {
        LOG.error("Error writing JUnit test class to {}", outputFile);
      }
    }
  }

  public void generateCucumberMain(String outputDirectory, String packageName) {
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    velocityEngine.setProperty(
        "classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    velocityEngine.init();
    Template t = velocityEngine.getTemplate("cucumberMain.vm");
    VelocityContext context = new VelocityContext();
    context.put("packageName", packageName);
    File outputFile =
        new File(
            outputDirectory + "/" + packageName.replaceAll("\\.", "\\/") + "/CucumberMain.java");
    LOG.info("Writing CucumberMain class to {}", outputFile);
    outputFile.getParentFile().mkdirs();
    try (FileWriter writer = new FileWriter(outputFile)) {
      t.merge(context, writer);
      writer.flush();
    } catch (IOException e) {
      LOG.error("Error writing CucumberMain class to {}", outputFile);
    }
  }
}
