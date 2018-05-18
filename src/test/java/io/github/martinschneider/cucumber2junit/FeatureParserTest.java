package io.github.martinschneider.cucumber2junit;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.martinschneider.cucumber2junit.model.Feature;
import java.util.List;
import org.junit.Test;

/** Unit tests for {@link FeatureParser}. */
public class FeatureParserTest {

  private FeatureParser target = new FeatureParser();

  @Test
  public void testParseFeature() {
    String path = this.getClass().getClassLoader().getResource("features").getPath();
    List<Feature> features = target.parseFeatures(path, "ios,", "android", "web");
    assertThat(features.size()).as("Check number of features").isEqualTo(3);
    assertThat(features)
        .containsExactly(
            TestDataProvider.getFeature1(),
            TestDataProvider.getFeature2(),
            TestDataProvider.getFeature3());
  }

  @Test
  public void testParseFeatureFilterTags() {
    String path = this.getClass().getClassLoader().getResource("features").getPath();
    List<Feature> features = target.parseFeatures(path, "ios");
    assertThat(features.size()).as("Check number of features").isEqualTo(2);
    assertThat(features)
        .containsExactly(TestDataProvider.getFeature2IosOnly(), TestDataProvider.getFeature3());
  }

  @Test
  public void testParseTags() {
    assertThat(FeatureParser.parseTags("@tag1    @tag2 @tag3"))
        .as("Check multiple tags")
        .containsExactly("tag1", "tag2", "tag3");
    assertThat(FeatureParser.parseTags("@tag4")).as("Check single tag").containsExactly("tag4");
    assertThat(FeatureParser.parseTags("")).as("Check empty tags").isEmpty();
    assertThat(FeatureParser.parseTags("   ")).as("Check empty tags").isEmpty();
  }

  @Test
  public void testParseFeatureName() {
    assertThat(FeatureParser.parseFeatureName("   Feature: my wonderful feature    "))
        .as("check feature name")
        .isEqualTo("my wonderful feature");
  }

  @Test
  public void testParseScenarioName() {
    assertThat(FeatureParser.parseScenarioName("  Scenario: my wonderful scenario     "))
        .as("check scenario name")
        .isEqualTo("my wonderful scenario");
  }
}
