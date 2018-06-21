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
import org.apache.commons.lang3.tuple.Pair;
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

  private List<Feature> parseFeature(
      Path path, Path rootPath, List<Feature> features, String... tags) {
    LOG.info("Processing feature file {}", path);
    try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
      List<Scenario> scenarios = new ArrayList<Scenario>();
      List<String> featureTags = new ArrayList<String>();
      List<String> includedTags = parseRequiredTags(tags).getLeft();
      List<String> excludedTags = parseRequiredTags(tags).getRight();
      String featureName = "";
      String line = "";
      String previousLine = "";
      int lineNumber = 1;
      LOG.info("Required tags: {}, forbidden tags: {}", includedTags, excludedTags);
      while ((line = reader.readLine()) != null) {
        if (line.trim().startsWith("Feature")) {
          featureTags = parseTags(previousLine);
          featureName = parseFeatureName(line);
        }
        if (line.trim().startsWith("Scenario")) {
          Scenario scenario = parseScenario(line, previousLine, lineNumber);
          List<String> allTags = new ArrayList<String>(featureTags);
          allTags.addAll(scenario.getTags());
          if (includedTags.stream().allMatch(allTags::contains)
              && excludedTags.stream().noneMatch(allTags::contains)) {
            LOG.info("Adding scenario {} with tags {}", scenario, allTags);
            scenarios.add(scenario);
          } else {
            LOG.debug("Skipping scenario {}", scenario);
          }
        }
        previousLine = line;
        lineNumber++;
      }
      if (!scenarios.isEmpty()) {
        features.add(
            new Feature(featureName, rootPath.relativize(path).toString(), scenarios, featureTags));
      }
      if (scenarios.size() == 1) {
        LOG.info("1 matching scenario added");
      } else {
        LOG.info("{} matching scenarios added", scenarios.size());
      }
    } catch (IOException e) {
      LOG.warn("Error reading feature file {}. Skipping!");
    }
    return features;
  }

  // the left side of the Pair includes the required tags, the right side the forbidden ones
  private Pair<List<String>, List<String>> parseRequiredTags(String[] input) {
    List<String> requiredTags = new ArrayList<String>();
    List<String> forbiddenTags = new ArrayList<String>();
    Pair<List<String>, List<String>> tags = Pair.of(requiredTags, forbiddenTags);
    for (String tag : input) {
      if (tag.startsWith("~")) {
        forbiddenTags.add(tag.substring(1));
      } else {
        requiredTags.add(tag);
      }
    }
    return tags;
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
