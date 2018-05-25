package io.github.martinschneider.cucumber2junit;

import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Mojo for the Maven plugin. */
@Mojo(name = "generate")
public class CodeGenerationMojo extends AbstractMojo {

  private static final Logger LOG = LoggerFactory.getLogger(CodeGenerationMojo.class); 
  
  @Parameter(defaultValue = "${project.basedir}/src/test/resources/features")
  private String featuresSourceDirectory;

  @Parameter(defaultValue = "${project.basedir}/src/test/java")
  private String javaOutputDirectory;

  @Parameter(defaultValue = "features")
  private String featuresClasspath;

  @Parameter(defaultValue = "/tmp/features")
  private String featuresOutputDirectory;

  @Parameter(required = true)
  private String packageName;

  @Parameter(required = true)
  private String stepsPackageName;

  @Parameter private String templatePath;

  @Parameter(property = "tags")
  private String[] tags;
  
  @Parameter
  private Map<String, String> additionalPlaceholders;

  private FeatureParser featureParser = new FeatureParser();
  private JUnitTestGenerator codeGenerator = new JUnitTestGenerator();

  public void execute() throws MojoExecutionException {
    LOG.info("Generating JUnit test classes...");
    LOG.info("featuresSourceDirectory = {}", featuresSourceDirectory);
    LOG.info("javaOutputDirectory = {}", javaOutputDirectory);
    LOG.info("featuresClasspath = {}", featuresClasspath);
    LOG.info("packageName = {}", packageName);
    LOG.info("stepsPackageName = {}", stepsPackageName);
    LOG.info("templatePath = {}", templatePath);
    LOG.info("tags = {}", tags);
    codeGenerator.generateCucumberMain(javaOutputDirectory, packageName);
    codeGenerator.generateJUnitTests(
        featureParser.parseFeatures(featuresSourceDirectory, tags),
        featuresSourceDirectory,
        javaOutputDirectory,
        featuresOutputDirectory,
        featuresClasspath,
        packageName,
        stepsPackageName,
        templatePath,
        additionalPlaceholders);
  }
}
