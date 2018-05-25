package io.github.martinschneider.cucumber2junit;

import io.github.martinschneider.cucumber2junit.model.Feature;
import io.github.martinschneider.cucumber2junit.model.Scenario;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Parser for feature files. */
public class FeatureParser {

  private static final Logger LOG = LoggerFactory.getLogger(FeatureParser.class);

  /**
   * Parse features from a given directory. Only scenarios which have at least one of the specified
   * tags will be included. Scenarios "inherit" the tags of the feature they are defined in.
   *
   * @param path directory containing the feature files
   * @param tags {@link List} of tags to include
   * @return {@link List} of {@link Feature}s
   */
  public List<Feature> parseFeatures(String path, String... tags) {
    List<Feature> features = new ArrayList<Feature>();
    LOG.info("Recursively parsing feature files from {}", path);
    try (Stream<Path> paths = Files.walk(Paths.get(path))) {
      paths
          .filter(Files::isRegularFile)
          .forEach(x -> parseFeature(x, Paths.get(path), features, tags));
    } catch (IOException e) {
      LOG.error("Couldn't load feature files from directory {}", path);
    }
    return features;
  }

  static List<Feature> parseFeature(
      Path path, Path rootPath, List<Feature> features, String... tags) {
    LOG.info("Processing feature file {}", path);
    try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
      List<Scenario> scenarios = new ArrayList<Scenario>();
      List<String> featureTags = new ArrayList<String>();
      String featureName = "";
      String line = "";
      String previousLine = "";
      int lineNumber = 1;
      while ((line = reader.readLine()) != null) {
        if (line.trim().startsWith("Feature")) {
          featureTags = parseTags(previousLine);
          featureName = parseFeatureName(line);
        }
        if (line.trim().startsWith("Scenario")) {
          Scenario scenario = parseScenario(line, previousLine, lineNumber);
          LOG.info("Scenario tags: {}, required tags: {}", scenario.getTags(), tags);
          if (tags.length == 0
              || (Arrays.asList(tags).stream().allMatch(scenario.getTags()::contains)
                  || Arrays.asList(tags).stream().allMatch(featureTags::contains))) {
            LOG.info("Adding scenario {}", scenario);
            scenarios.add(scenario);
          }
          else
          {
            LOG.info("Skipping scenario {}", scenario);
          }
        }
        previousLine = line;
        lineNumber++;
      }
      if (!scenarios.isEmpty()) {
        features.add(
            new Feature(featureName, rootPath.relativize(path).toString(), scenarios, featureTags));
      }
    } catch (IOException e) {
      LOG.warn("Error reading feature file {}. Skipping!");
    }
    return features;
  }

  static String parseFeatureName(String line) {
    return line.replaceAll("Feature: ", "").trim();
  }

  static String parseScenarioName(String line) {
    return line.replaceAll("Scenario( Outline)?: ", "").trim();
  }

  static Scenario parseScenario(String line, String previousLine, int lineNumber) {
    return new Scenario(lineNumber, parseScenarioName(line), parseTags(previousLine));
  }

  static List<String> parseTags(String line) {
    List<String> tags = new ArrayList<String>();
    if (line.isEmpty()) {
      return Collections.emptyList();
    } else {
      String[] tagsArray = line.trim().replaceAll(" +", " ").split(" ");
      for (String tag : tagsArray) {
        if (!tag.isEmpty()) {
          tags.add(tag.trim().replaceAll("@", ""));
        }
      }
      return tags;
    }
  }
}
