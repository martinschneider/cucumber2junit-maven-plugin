package io.github.martinschneider.cucumber2junit.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.List;

/** DTO for Gherkin features. */
public class Feature {

  private String name;

  private String fileName;

  private List<Scenario> scenarios;

  private List<String> tags;

  /**
   * Constructor.
   *
   * @param name feature name
   * @param fileName the file name of the feature
   * @param scenarios {@link List} of scenarios
   * @param tags {@link List} of tags
   */
  public Feature(String name, String fileName, List<Scenario> scenarios, List<String> tags) {
    super();
    this.name = name;
    this.fileName = fileName;
    this.scenarios = scenarios;
    this.tags = tags;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Scenario> getScenarios() {
    return scenarios;
  }

  public void setScenarios(List<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, scenarios, tags, fileName);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Feature other = (Feature) obj;
    return Objects.equal(this.name, other.name)
        && Objects.equal(this.scenarios, other.scenarios)
        && Objects.equal(this.tags, other.tags)
        && Objects.equal(this.fileName, other.fileName);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("scenarios", scenarios)
        .add("tags", tags)
        .add("fileName", fileName)
        .toString();
  }
}
